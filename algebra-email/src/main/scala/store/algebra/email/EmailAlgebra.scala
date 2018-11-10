package store.algebra.email

import store.core.entity.Email
import store.effects.Concurrent

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait EmailAlgebra[F[_]] {

  def sendEmail(to: Email, subject: EmailSubject, content: EmailContent): F[Unit]

  def receiveEmail(fromEmail: Email, fromName: String, subject: EmailSubject, content: EmailContent): F[Unit]

}
object EmailAlgebra {

  def async[F[_]: Concurrent](config: EmailConfig)(
      implicit emailContext: EmailContext[F]): EmailAlgebra[F] =
    new impl.ConcurrentAlgebraImpl[F](config)

}
