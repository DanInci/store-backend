package store.algebra.product.impl

import java.time.LocalDate

import busymachines.core._
import cats.implicits._
import doobie._
import doobie.implicits._
import store.effects._
import store.db.DatabaseContext
import store.algebra.content._
import store.algebra.content.entity.Content
import store.algebra.product.entity._
import store.algebra.product._
import store.algebra.product.entity.component._
import store.core.BlockingAlgebra
import store.core.entity._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
final private[product] class AsyncAlgebraImpl[F[_]](
    contentStorageAlgebra: ContentStorageAlgebra[F])(
    implicit
    val F: Async[F],
    val transactor: Transactor[F],
    val dbContext: DatabaseContext[F]
) extends ProductAlgebra[F]
    with ProductStockAlgebra[F]
    with PromotionAlgebra[F]
    with BlockingAlgebra[F] {

  import store.algebra.product.db.ProductSql._

  override def getCategories(sex: Sex): F[List[Category]] = transact {
    findCategoriesBySex(sex).map(_.map(c =>
      Category(c.categoryId, c.name, Some(c.sex))))
  }

  /*_*/
  override def createProduct(
      productDefinition: StoreProductDefinition): F[ProductID] = {
    for {
      productId <- transact(insertProduct(productDefinition))
      contentIds <- productDefinition.images
        .map(
          i => checkAndSaveContent(i, Path(s"product/$productId"))
        )
        .sequence
      _ <- transact {
        for {
          _ <- contentIds
            .map(cid => mapContentToProduct(cid, productId))
            .sequence
          _ <- productDefinition.stocks
            .map(s => addStockToProduct(s, productId))
            .sequence
        } yield ()
      }
    } yield productId
  } /*_*/

  /*_*/
  override def getProducts(nameFilter: Option[String],
                           categoryFilter: List[CategoryID],
                           pagingInfo: PagingInfo): F[List[StoreProduct]] = {
    for {
      productsDB <- transact {
        for {
          productsDb <- findByNameAndCategories(nameFilter,
                                                categoryFilter,
                                                pagingInfo.offset,
                                                pagingInfo.limit)
          stocksList <- productsDb
            .map(
              p => findStocksByProductID(p.productId)
            )
            .sequence
          storeProducts = productsDb.mapWithIndex(
            (p, index) => StoreProduct.fromStoreProductDB(p, stocksList(index))
          )
        } yield storeProducts
      }
      products <- productsDB
        .map(
          p =>
            for {
              images <- getContentByProductId(p.productId, loadBytes = false)
            } yield p.copy(images = images)
        )
        .sequence
    } yield products
  } /*_*/

  override def getProduct(productId: ProductID): F[StoreProduct] = {
    for {
      product <- transact {
        for {
          productDB <- findById(productId).flatMap(
            exists(_, NotFoundFailure(s"Product not found wih id $productId")))
          stocks <- findStocksByProductID(productDB.productId)
        } yield StoreProduct.fromStoreProductDB(productDB, stocks)
      }
      images <- getContentByProductId(product.productId, loadBytes = false)
    } yield product.copy(images = images)
  }

  override def removeProduct(productId: ProductID): F[Unit] = {
    for {
      shouldDeleteFiles <- {
        val q = for {
          _ <- deleteStockByProductID(productId)
          _ <- deleteContentsByProductID(productId)
          affectedRows <- deleteProduct(productId)
        } yield affectedRows == 1
        transact(q)
      }
      _ <- if (shouldDeleteFiles)
        contentStorageAlgebra.removeContentsFromPath(
          Path("products/" + productId))
      else F.raiseError(NotFoundFailure(s"Product not found wih id $productId"))
    } yield ()
  }

  override def getStock(productId: ProductID): F[List[Stock]] = transact {
    for {
      productDB <- findById(productId).flatMap(
        exists(_, NotFoundFailure(s"Product not found wih id $productId")))
      stocks <- findStocksByProductID(productDB.productId)
    } yield stocks
  }

  override def addStock(stock: Stock, productId: ProductID): F[Unit] =
    transact {
      for {
        _ <- findById(productId).flatMap(
          exists(_, NotFoundFailure(s"Product not found wih id $productId")))
        currentStock <- findStockByProductIdAndSize(productId, stock.size)
        _ <- currentStock match {
          case Some(cs) =>
            updateStockByProductIDAndSize(cs.count + stock.count,
                                          productId,
                                          stock.size)
          case None => addStockToProduct(stock, productId)
        }
      } yield ()
    }

  override def removeStock(stock: Stock, productId: ProductID): F[Unit] =
    transact {
      for {
        _ <- findById(productId).flatMap(
          exists(_, NotFoundFailure(s"Product not found wih id $productId")))
        currentStock <- findStockByProductIdAndSize(productId, stock.size)
        _ <- currentStock match {
          case Some(cs) =>
            cs.count - stock.count match {
              case Left(_) =>
                AsyncConnectionIO.raiseError(
                  InvalidInputFailure(
                    "Can not remove stock because it will be negative"))
              case Right(c) =>
                updateStockByProductIDAndSize(c, productId, stock.size)
            }
          case None =>
            AsyncConnectionIO.raiseError(
              InvalidInputFailure("Can not remove stock if it does not exist"))
        }
      } yield ()
    }

  override def createPromotion(
      definition: PromotionDefinition): F[PromotionID] =
    for {
      contentId <- checkAndSaveContent(definition.image, Path("promotions"))
      promotionId <- transact {
        for {
          now <- AsyncConnectionIO.delay(LocalDate.now)
          _ <- if (now.isAfter(definition.expiresAt))
            AsyncConnectionIO.raiseError[Unit](
              InvalidInputFailure("Promotion expiration date already passed"))
          else AsyncConnectionIO.unit
          promotionId <- insertPromotion(definition, contentId)
        } yield promotionId
      }
    } yield promotionId

  override def getPromotionById(promotionId: PromotionID): F[Promotion] = {
    for {
      promotionDB <- transact(
        findPromotionById(promotionId).flatMap(
          exists(
            _,
            NotFoundFailure(s"Promotion with $promotionId was not found"))))
      promotion <- fillContentDBInfo(promotionDB.content, loadBytes = false)
        .map(c => Promotion.fromPromotionDB(promotionDB, c))
    } yield promotion
  }

  override def getAllPromotions: F[List[Promotion]] = {
    for {
      promotionDBList <- transact(findAllPromotionsOrderedByExpiresAtAsc)
      promotions <- promotionDBList
        .map(p =>
          fillContentDBInfo(p.content, loadBytes = false).map(c =>
            Promotion.fromPromotionDB(p, c)))
        .sequence
    } yield promotions
  }

  override def getActivePromotions: F[List[Promotion]] =
    for {
      allPromotions <- getAllPromotions
      activePromotions <- for {
        now <- F.delay(LocalDate.now)
        filteredPromotions = allPromotions.filter(p =>
          now.isBefore(p.expiresAt)) match {
          case Nil => filterLastChronologicalPromotions(allPromotions)
          case l   => l
        }
      } yield filteredPromotions
    } yield activePromotions

  override def deletePromotion(promotionId: PromotionID): F[Unit] =
    for {
      toBeDeletedContentId <- transact {
        for {
          promotion <- findPromotionById(promotionId).flatMap(
            exists(
              _,
              NotFoundFailure(s"Promotion with $promotionId was not found")))
          _ <- deletePromotionById(promotion.promotionId)
          _ <- deleteContentByID(promotion.content.contentId)
        } yield promotion.content.contentId
      }
      _ <- contentStorageAlgebra.removeContent(toBeDeletedContentId)
    } yield ()

  private def checkAndSaveContent(content: Content, path: Path): F[ContentID] =
    content.content match {
      case Left(cid) =>
        transact(
          findContentByID(cid)
            .flatMap(
              exists(
                _,
                InvalidInputFailure(s"Content with id $cid was not found")))
            .map(_.contentId)
        )
      case Right(binary) =>
        for {
          contentId <- contentStorageAlgebra.saveContent(path, content.format, binary)
          contentDB = ContentDB(contentId, content.name, content.format)
          _ <- transact(insertContent(contentDB))
        } yield contentId

    }

  private def getContentByProductId(productId: ProductID,
                                    loadBytes: Boolean): F[List[Content]] = {
    for {
      contentsDB <- transact(findContentsByProductID(productId))
      contents <- contentsDB
        .map(
          c =>
            if (loadBytes)
              contentStorageAlgebra
                .getContent(c.contentId)
                .map(binaryContent =>
                  Content(c.name, Right(binaryContent), c.format))
            else F.pure(Content(c.name, Left(c.contentId), c.format))
        )
        .sequence
    } yield contents
  }

  private def fillContentDBInfo(c: ContentDB,
                                loadBytes: Boolean): F[Content] = {
    if (loadBytes)
      contentStorageAlgebra
        .getContent(c.contentId)
        .map(binary => Content(c.name, Right(binary), c.format))
    else F.pure(Content(c.name, Left(c.contentId), c.format))
  }

  private def filterLastChronologicalPromotions(
      promotions: List[Promotion]): List[Promotion] = {
    val lastChronologicalExpirationDate = promotions.headOption.map(_.expiresAt)
    lastChronologicalExpirationDate match {
      case None      => Nil
      case Some(exp) => promotions.filter(_.expiresAt == exp)
    }
  }

  private def transact[A](query: ConnectionIO[A]): F[A] = {
    block(query.transact(transactor))
  }

  private def exists[A](value: Option[A],
                        failure: AnomalousFailure): ConnectionIO[A] =
    value match {
      case Some(x) => AsyncConnectionIO.pure[A](x)
      case None    => AsyncConnectionIO.raiseError(failure)
    }
}
