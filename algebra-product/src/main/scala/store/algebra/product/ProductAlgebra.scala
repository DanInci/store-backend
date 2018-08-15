package store.algebra.product

import doobie.util.transactor.Transactor
import store.algebra.content.ContentStorageAlgebra
import store.algebra.product.entity.{StoreProduct, StoreProductDefinition}
import store.core.entity.PagingInfo
import store.core._
import store.db.DatabaseContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ProductAlgebra[F[_]] {

  def createProduct(product: StoreProductDefinition): F[ProductID]

  def getProducts(nameFilter: Option[String], categoryFilter: List[CategoryID], pagingInfo: PagingInfo = PagingInfo.defaultPagingInfo): F[List[StoreProduct]]

  def getProduct(productId: ProductID): F[StoreProduct]

  def removeProduct(productId: ProductID): F[Unit]

}

object ProductAlgebra {
  import store.effects._

  def async[F[_]: Async](implicit transactor: Transactor[F], dbContext: DatabaseContext[F], contentAlgebra: ContentStorageAlgebra[F]): ProductAlgebra[F] = new impl.AsyncAlgebraImpl[F]()
}
