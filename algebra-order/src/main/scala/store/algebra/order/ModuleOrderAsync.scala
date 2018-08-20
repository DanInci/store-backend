package store.algebra.order

import doobie.util.transactor.Transactor
import store.db.DatabaseContext
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
trait ModuleOrderAsync[F[_]] {

  implicit def async: Async[F]

  implicit def transactor: Transactor[F]

  implicit def dbContext: DatabaseContext[F]

  def orderAlgebra: OrderAlgebra[F] = _orderAlgebra

  private lazy val _orderAlgebra: OrderAlgebra[F] =
    new impl.AsyncAlgebraImpl[F]()

}
