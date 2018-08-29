package store.algebra.product.impl

import busymachines.core._
import cats.implicits._
import doobie._
import doobie.implicits._
import store.effects._
import store.db.DatabaseContext
import store.algebra.content._
import store.algebra.content.entity.ContentDB
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
    with BlockingAlgebra[F] {

  import store.algebra.product.db.ProductSql._

  private lazy val _productFolder = "products"

  override def getCategories: F[List[Category]] = transact {
    findAllCategories
  }

  override def createProduct(
      productDefinition: StoreProductDefinition): F[ProductID] = {
    for {
      productId <- transact(insertProduct(productDefinition))
      imageFilesDB <- productDefinition.images
        .map(
          i =>
            contentStorageAlgebra
              .saveContent(Path(_productFolder + "/" + productId + "/"),
                           i.format,
                           i.content)
              .map(ContentDB(_, i.name)))
        .sequence
      _ <- {
        val q = for {
          _ <- imageFilesDB
            .map(i => addContentToProduct(i, productId))
            .sequence
          _ <- productDefinition.stocks
            .map(s => addStockToProduct(s, productId))
            .sequence
        } yield ()
        transact(q)
      }
    } yield productId
  }

  override def getProducts(nameFilter: Option[String],
                           categoryFilter: List[CategoryID],
                           pagingInfo: PagingInfo): F[List[StoreProduct]] = {
    for {
      productImagesDBList <- {
        val q = for {
          productsDb <- findByNameAndCategories(nameFilter,
                                                categoryFilter,
                                                pagingInfo.offset,
                                                pagingInfo.limit)
          imagesDBList <- productsDb
            .map(
              p =>
                findContentByProductID(p.productId, ImageFile.format)
            )
            .sequence
          stocksList <- productsDb
            .map(
              p => findStocksByProductID(p.productId)
            )
            .sequence
          storeProducts = productsDb.mapWithIndex(
            (p, index) => StoreProduct.fromStoreProductDB(p, stocksList(index))
          )
        } yield storeProducts zip imagesDBList
        transact(q)
      }
      products <- productImagesDBList.map {
        case (p: StoreProduct, imagesDB: List[ContentDB]) =>
          for {
            images <- imagesDB
              .map(
                i =>
                  contentStorageAlgebra
                    .getContentLink(i.contentId)
                    .map(ImageFileLink(i.name, _)))
              .sequence
            product = p.copy(images = images)
          } yield product
      }.sequence
    } yield products
  }

  override def getProduct(productId: ProductID): F[StoreProduct] = {
    for {
      (product, imageFilesDB) <- {
        val q = for {
          productDB <- findById(productId).flatMap(
            exists(_, NotFoundFailure(s"Product not found wih id $productId")))
          imagesDB <- findContentByProductID(productId,
                                                        ImageFile.format)
          stocks <- findStocksByProductID(productDB.productId)
        } yield (StoreProduct.fromStoreProductDB(productDB, stocks), imagesDB)
        transact(q)
      }
      images <- imageFilesDB
        .map(
          i =>
            contentStorageAlgebra
              .getContentLink(i.contentId)
              .map(ImageFileLink(i.name, _)))
        .sequence
    } yield product.copy(images = images)
  }

  override def removeProduct(productId: ProductID): F[Unit] = {
    for {
      shouldDeleteFiles <- {
        val q = for {
          _ <- deleteStockByProductID(productId)
          _ <- deleteContentByProductID(productId)
          affectedRows <- deleteProduct(productId)
        } yield affectedRows == 1
        transact(q)
      }
      _ <- if (shouldDeleteFiles)
        contentStorageAlgebra.removeContentsFromPath(
          Path(_productFolder + "/" + productId))
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
            updateStockByProductIDAndSize(Count(cs.count + stock.count),
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
            val updatedCount = Count(cs.count - stock.count)
            if (updatedCount < 0)
              AsyncConnectionIO.raiseError(
                InvalidInputFailure(
                  "Can not remove stock because it will be negative"))
            else
              updateStockByProductIDAndSize(updatedCount, productId, stock.size)
          case None =>
            AsyncConnectionIO.raiseError(
              InvalidInputFailure("Can not remove stock if it does not exist"))
        }
      } yield ()
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
