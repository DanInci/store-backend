package store.algebra.email

import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait ModuleEmailAsync[F[_]] {

  implicit def async: Async[F]

  def emailConfig: EmailConfig

  def emailAlgebra: EmailAlgebra[F] = _emailAlgebra

  private lazy val _emailAlgebra: EmailAlgebra[F] =
    new impl.AsyncAlgebraImpl[F](emailConfig)

}
