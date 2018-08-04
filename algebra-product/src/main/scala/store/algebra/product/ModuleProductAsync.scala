package store.algebra.product

import store.effects._
import store.db.DatabaseContext
import doobie.util.transactor.Transactor

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ModuleProductAsync[F[_]] {

  implicit def async: Async[F]

  implicit def transactor: Transactor[F]

  implicit def dbContext: DatabaseContext[F]

  def productAlgebra: ProductAlgebra[F] = _moduleAlgebra

  def productModuleAlgebra: ProductAlgebra[F] = _moduleAlgebra

  private lazy val _moduleAlgebra: ProductAlgebra[F] = new impl.AsyncAlgebraImpl[F]()

}
