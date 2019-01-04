package store.algebra.product.impl

import busymachines.core._
import cats.implicits._
import doobie._
import doobie.implicits._
import store.effects._
import store.db._
import store.algebra.content._
import store.algebra.content.entity.Content
import store.algebra.product.entity._
import store.algebra.product._
import store.algebra.product.db.entity._
import store.algebra.product.entity.component._
import store.core._
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
    with DatabaseAlgebra[F]
    with BlockingAlgebra[F] {

  import store.algebra.product.db.ProductSql._

  override def getCategories(sexFilter: Option[Sex]): F[List[Category]] =
    transact {
      val categories = sexFilter match {
        case Some(sex) => findCategoriesBySex(sex)
        case None      => findCategories
      }
      categories.map(_.map(c => Category(c.categoryId, c.name, Some(c.sex))))
    }

  override def createCategory(definition: CategoryDefinition): F[CategoryID] =
    transact {
      for {
        categories <- findCategoriesBySex(definition.sex)
        _ <- if (categories.exists(_.name == definition.name))
          raiseError(InvalidInputFailure(
            s"Category with name ${definition.name} of sex ${definition.sex} already exists"))
        else AsyncConnectionIO.unit
        categoryId <- insertCategory(definition)
      } yield categoryId
    }

  override def removeCategory(categoryId: CategoryID): F[Unit] = transact {
    for {
      _ <- findCategoryById(categoryId).flatMap(
        exists(_,
               NotFoundFailure(s"Category with id $categoryId was not found")))
      _ <- deleteCategory(categoryId)
    } yield ()
  }

  /*_*/
  override def createProduct(
      productDefinition: StoreProductDefinition): F[ProductID] = {
    for {
      productId <- transact {
        for {
          _ <- findCategoryById(productDefinition.categoryId).flatMap(exists(
            _,
            InvalidInputFailure(
              s"Cateogory with id ${productDefinition.categoryId} does not exist")))
          pid <- insertProduct(productDefinition)
        } yield pid
      }
      contentIds <- productDefinition.images
        .map(
          checkAndSaveContent(_,
                              Path(s"product/$productId"),
                              isPromotion = false))
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
          productsDb <- findByNameAndCategoriesOrderedByAddedAtDesc(
            nameFilter,
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

  override def getProductsCount(nameFilter: Option[String], categoryFilter: List[CategoryID]): F[Count] = transact {
    for {
      count <- countByNameAndCategories(nameFilter, categoryFilter)
    } yield count
  }

  override def getProduct(productId: ProductID): F[StoreProduct] = {
    for {
      product <- transact {
        for {
          productDB <- findById(productId).flatMap(
            exists(
              _,
              NotFoundFailure(s"Product with id $productId was not found")))
          stocks <- findStocksByProductID(productDB.productId)
        } yield StoreProduct.fromStoreProductDB(productDB, stocks)
      }
      images <- getContentByProductId(product.productId, loadBytes = false)
    } yield product.copy(images = images)
  }

  override def removeProduct(productId: ProductID): F[Unit] = {
    for {
      _ <- transact {
        for {
          product <- findById(productId).flatMap(
            exists(
              _,
              NotFoundFailure(s"Product with id $productId was not found")))
          _ <- deleteStockByProductID(productId)
          _ <- deleteContentsByProductID(productId)
          _ <- deleteProduct(productId)
        } yield ()
      }
      _ <- contentStorageAlgebra.removeContentsFromPath(
        Path("products/" + productId))
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

  override def getPromotions: F[List[Content]] =
    for {
      contentListDB <- transact(findPromotionContent)
      promotions <- contentListDB
        .map(c => fillContentDBInfo(c, loadBytes = false))
        .sequence
    } yield promotions

  override def createPromotion(content: Content): F[ContentID] =
    checkAndSaveContent(content, Path(s"promotion"), isPromotion = true)

  override def removePromotion(contentId: ContentID): F[Unit] =
    for {
      id <- transact {
        for {
          allPromotions <- findPromotionContent
          maybePromotion = allPromotions.find(_.contentId.contains(contentId))
          promotion <- exists(
            maybePromotion,
            NotFoundFailure(
              s"Promotional content with $contentId was not found"))
          _ <- deleteContentByID(promotion.contentId)
        } yield promotion.contentId
      }
      _ <- contentStorageAlgebra.removeContent(id)
    } yield ()

  /*_*/
  override def getProductNavigation(
      currentProductId: ProductID): F[ProductNavigation] =
    for {
      (previous, current, next) <- transact {
        for {
          current <- findById(currentProductId).flatMap(
            exists(_,
                   NotFoundFailure(
                     s"Product with id $currentProductId was not found")))
          currentStocks <- findStocksByProductID(current.productId)
          previous: Option[StoreProduct] <- findPreviousByCurrentId(
            currentProductId).flatMap(_.map(fillStoreProductDBInfo).sequence)
          next: Option[StoreProduct] <- findNextByCurrentId(currentProductId)
            .flatMap(_.map(fillStoreProductDBInfo).sequence)
        } yield
          (previous,
           StoreProduct.fromStoreProductDB(current, currentStocks),
           next)
      }
      current <- getContentByProductId(current.productId, loadBytes = false)
        .map(images => current.copy(images = images))
      previous <- previous
        .map(p =>
          getContentByProductId(p.productId, loadBytes = false).map(images =>
            p.copy(images = images)))
        .sequence
      previous <- previous
        .map(n =>
          getContentByProductId(n.productId, loadBytes = false).map(images =>
            n.copy(images = images)))
        .sequence
    } yield ProductNavigation(previous, current, next)
  /*_*/

  private def checkAndSaveContent(content: Content,
                                  path: Path,
                                  isPromotion: Boolean): F[ContentID] =
    content.getContent match {
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
          contentId <- contentStorageAlgebra.saveContent(path,
                                                         content.format,
                                                         binary)
          contentDB = ContentDB(contentId,
                                content.name,
                                content.format,
                                isPromotion)
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
                  Content.fromBinary(c.name, binaryContent, c.format))
            else F.pure(Content.fromContentID(c.name, c.contentId, c.format))
        )
        .sequence
    } yield contents
  }

  private def fillStoreProductDBInfo(
      s: StoreProductDB): ConnectionIO[StoreProduct] =
    findStocksByProductID(s.productId).map(stocks =>
      StoreProduct.fromStoreProductDB(s, stocks))

  private def fillContentDBInfo(c: ContentDB,
                                loadBytes: Boolean): F[Content] = {
    if (loadBytes)
      contentStorageAlgebra
        .getContent(c.contentId)
        .map(binary => Content.fromBinary(c.name, binary, c.format))
    else F.pure(Content.fromContentID(c.name, c.contentId, c.format))
  }

  private def exists[A](value: Option[A],
                        failure: AnomalousFailure): ConnectionIO[A] =
    value match {
      case Some(x) => pure[A](x)
      case None    => raiseError(failure)
    }
}
