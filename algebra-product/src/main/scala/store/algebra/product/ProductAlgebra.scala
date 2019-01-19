package store.algebra.product

import doobie.util.transactor.Transactor
import store.algebra.content.ContentStorageAlgebra
import store.algebra.product.entity.component._
import store.algebra.product.entity._
import store.core.entity.MonthsAge
import store.core.entity.PagingInfo
import store.db.DatabaseContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ProductAlgebra[F[_]] {

  def createProduct(product: StoreProductDefinition): F[ProductID]

  def updateProduct(productId: ProductID, updates: StoreProductDefinition): F[ProductID]

  def getProducts(nameFilter: Option[String], categoryFilter: List[CategoryID], ageFilter: Option[MonthsAge], pagingInfo: PagingInfo = PagingInfo.defaultPagingInfo): F[List[StoreProduct]]

  def getProductsCount(nameFilter: Option[String], categoryFilter: List[CategoryID], ageFilter: Option[MonthsAge]): F[Count]

  def getProduct(productId: ProductID): F[StoreProduct]

  def getProductNavigation(currentProductId: ProductID, nameFilter: Option[String], categoryFilter: List[CategoryID], ageFilter: Option[MonthsAge]): F[ProductNavigation]

  def removeProduct(productId: ProductID): F[Unit]

}

object ProductAlgebra {
  import store.effects._

  def async[F[_]: Async](contentAlgebra: ContentStorageAlgebra[F])(implicit transactor: Transactor[F], dbContext: DatabaseContext[F]): ProductAlgebra[F] = new impl.AsyncAlgebraImpl[F](contentAlgebra)
}
