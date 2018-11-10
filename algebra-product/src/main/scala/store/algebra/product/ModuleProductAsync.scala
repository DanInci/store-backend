package store.algebra.product

import store.effects._
import store.db.DatabaseContext
import doobie.util.transactor.Transactor
import store.algebra.content.ModuleContentAsync

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ModuleProductAsync[F[_]] { this: ModuleContentAsync[F] =>

  implicit def async: Async[F]

  implicit def transactor: Transactor[F]

  implicit def dbContext: DatabaseContext[F]

  def productAlgebra: ProductAlgebra[F] = _moduleAlgebra

  def stockAlgebra: ProductStockAlgebra[F] = _moduleAlgebra

  private lazy val _moduleAlgebra: ModuleProductAlgebra[F] =
    new impl.AsyncAlgebraImpl[F](contentStorageAlgebra = s3StorageAlgebra)

}
