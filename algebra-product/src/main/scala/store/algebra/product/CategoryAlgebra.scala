package store.algebra.product

import doobie.util.transactor.Transactor
import store.algebra.content.ContentStorageAlgebra
import store.algebra.product.entity.component._
import store.db.DatabaseContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 19/01/2019
  */
trait CategoryAlgebra[F[_]] {

  def getCategories(sexFilter: Option[Sex]): F[List[Category]]

  def createCategory(categoryDefinition: CategoryDefinition): F[CategoryID]

  def removeCategory(categoryId: CategoryID): F[Unit]

}

object CategoryAlgebra {
  import store.effects._

  def async[F[_]: Async](contentAlgebra: ContentStorageAlgebra[F])(implicit transactor: Transactor[F], dbContext: DatabaseContext[F]): CategoryAlgebra[F] = new impl.AsyncAlgebraImpl[F](contentAlgebra)
}
