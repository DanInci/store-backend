package store.algebra.product

import doobie.util.transactor.Transactor
import store.db.{DatabaseAlgebra, DatabaseContext}

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
class ProductAlgebra[F[_]] extends DatabaseAlgebra[F] {

}

object ProductAlgebra {
  import store.effects._

  def async[F[_]: Async](implicit transactor: Transactor[F], dbContext: DatabaseContext[F]): ProductAlgebra[F] = new impl.AsyncAlgebraImpl[F]()
}
