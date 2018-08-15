package store.algebra.product.impl

import busymachines.core._
import cats.implicits._
import doobie._
import doobie.implicits._
import store.effects._
import store.db.{BlockingAlgebra, DatabaseContext}
import store.algebra.content.ContentStorageAlgebra
import store.algebra.product.entity._
import store.algebra.product._
import store.algebra.product.db.entity.ImageFileDB
import store.algebra.product.entity.component.ImageFile
import store.core._
import store.core.entity._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
final private[product] class AsyncAlgebraImpl[F[_]](
    implicit
    val F: Async[F],
    val transactor: Transactor[F],
    val dbContext: DatabaseContext[F],
    val contentStorageAlgebra: ContentStorageAlgebra[F]
) extends ProductAlgebra[F]
    with ProductStockAlgebra[F]
    with BlockingAlgebra[F] {

  import store.algebra.product.db.ProductSql._

  override def createProduct(
      productDefinition: StoreProductDefinition): F[ProductID] = {
    for {
      productId <- transact(insertProduct(productDefinition))
      imageFilesDB <- productDefinition.images
        .map(
          i =>
            contentStorageAlgebra
              .saveContent(i.content, productId)
              .map(ImageFileDB(_, i.name)))
        .sequence
      _ <- {
        val q = for {
          _ <- imageFilesDB
            .map(i => addImageFileDBToProduct(i, productId))
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
      productImageFilesDBList <- {
        val q = for {
          productsDb <- findByNameAndCategories(nameFilter,
                                                categoryFilter,
                                                pagingInfo.offset,
                                                pagingInfo.limit)
          imageFilesDBList <- productsDb.map(
            p => findImageFilesDBByProductID(p.productId)
          ).sequence
          stocksList <- productsDb.map(
            p => findStocksByProductID(p.productId)
          ).sequence
          storeProducts = productsDb.mapWithIndex(
            (p, index) => StoreProduct.fromStoreProductDB(p, stocksList(index))
          )
        } yield storeProducts zip imageFilesDBList
        transact(q)
      }
      products <- productImageFilesDBList.map {
        case (p: StoreProduct, imagesDB: List[ImageFileDB]) =>
          for {
            images <- imagesDB.map(i => contentStorageAlgebra
                                              .getContent(i.contentId)
            .map(ImageFile(i.name, _))).sequence
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
          imageFilesDB <- findImageFilesDBByProductID(productId)
          stocks <- findStocksByProductID(productDB.productId)
        } yield
          (StoreProduct.fromStoreProductDB(productDB, stocks), imageFilesDB)
        transact(q)
      }
      images <- imageFilesDB
        .map(
          i =>
            contentStorageAlgebra
              .getContent(i.contentId)
              .map(ImageFile(i.name, _)))
        .sequence
    } yield product.copy(images = images)
  }

  override def removeProduct(productId: ProductID): F[Unit] = {
    for {
      shouldDeleteFiles <- {
        val q = for {
          _ <- deleteStockByProductID(productId)
          _ <- deleteImageFileDBByProductID(productId)
          affectedRows <- deleteProduct(productId)
        } yield affectedRows == 1
        transact(q)
      }
      _ <- if (shouldDeleteFiles)
        contentStorageAlgebra.removeContentForProduct(productId)
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
