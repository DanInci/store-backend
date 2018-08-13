package store.algebra.product.impl

import store.effects._
import store.db.{BlockingAlgebra, DatabaseContext}
import doobie.Transactor
import store.algebra.content.ContentStorageAlgebra
import store.algebra.product.entity.{Stock, StoreProduct}
import store.algebra.product._
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
) extends ProductAlgebra[F] with ProductStockAlgebra[F] with BlockingAlgebra[F]{

  override def createProduct(product: StoreProduct): F[ProductID] = ???

  override def getProducts(nameFilter: Option[String], categoryFilter: List[CategoryID], pagingInfo: PagingInfo): F[List[StoreProduct]] = ???

  override def getProduct(productId: ProductID): F[Option[StoreProduct]] = ???

  override def removeProduct(productId: ProductID): F[Unit] = ???

  override def getStock(productID: ProductID): F[List[Stock]] = ???

  override def addStock(stock: Stock, productId: ProductID): F[Unit] = ???

  override def removeStock(stock: Stock, productId: ProductID): F[Unit] = ???
}
