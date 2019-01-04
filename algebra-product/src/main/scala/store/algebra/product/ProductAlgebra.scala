package store.algebra.product

import doobie.util.transactor.Transactor
import store.algebra.content.{ContentID, ContentStorageAlgebra}
import store.algebra.content.entity.Content
import store.algebra.product.entity.component._
import store.algebra.product.entity._
import store.core.entity.PagingInfo
import store.db.DatabaseContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ProductAlgebra[F[_]] {

  def getCategories(sexFilter: Option[Sex]): F[List[Category]]

  def createCategory(categoryDefinition: CategoryDefinition): F[CategoryID]

  def removeCategory(categoryId: CategoryID): F[Unit]

  def getPromotions: F[List[Content]]

  def removePromotion(contentId: ContentID): F[Unit]

  def createPromotion(content: Content): F[ContentID]

  def createProduct(product: StoreProductDefinition): F[ProductID]

  def getProducts(nameFilter: Option[String], categoryFilter: List[CategoryID], pagingInfo: PagingInfo = PagingInfo.defaultPagingInfo): F[List[StoreProduct]]

  def getProductsCount(nameFilter: Option[String], categoryFilter: List[CategoryID]): F[Count]

  def getProduct(productId: ProductID): F[StoreProduct]

  def getProductNavigation(currentProductId: ProductID): F[ProductNavigation]

  def removeProduct(productId: ProductID): F[Unit]

}

object ProductAlgebra {
  import store.effects._

  def async[F[_]: Async](contentAlgebra: ContentStorageAlgebra[F])(implicit transactor: Transactor[F], dbContext: DatabaseContext[F]): ProductAlgebra[F] = new impl.AsyncAlgebraImpl[F](contentAlgebra)
}
