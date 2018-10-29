package store.algebra.email

import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait ModuleEmailConcurrent[F[_]] {

  implicit def concurrent: Concurrent[F]

  implicit def emailContext: EmailContext[F]

  def emailConfig: EmailConfig

  def emailAlgebra: EmailAlgebra[F] = _emailAlgebra

  private lazy val _emailAlgebra: EmailAlgebra[F] =
    new impl.ConcurrentAlgebraImpl[F](emailConfig)

}
