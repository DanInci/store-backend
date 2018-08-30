package store.algebra.email

import store.core.entity.Email
import store.effects.Async

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait EmailAlgebra[F[_]] {

  def sendEmail(to: Email, subject: Subject, content: Content): F[Unit]

  def receiveMail(from: Email, subject: Subject, content: Content): F[Unit]

}
object EmailAlgebra {

  def async[F[_]: Async](config: EmailConfig): EmailAlgebra[F] =
    new impl.AsyncAlgebraImpl[F](config)

}
