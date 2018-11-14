package store.service.order.rest

import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import store.effects._
import store.http._
import store.algebra.email._
import store.algebra.order.entity._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 30/08/2018
  */
final class ContactRestService[F[_]](
    emailAlgebra: EmailAlgebra[F]
)(
    implicit F: Async[F]
) extends Http4sDsl[F] with OrderServiceJSON {

  val service: HttpService[F] = HttpService {
    case req @ POST -> Root / "contact" =>
      for {
        contact <- req.as[ContactRequest]
        receivedSubject = EmailSubject(s"${contact.name} sent you an email")
        receivedContent = EmailContent(s"Contact email: ${contact.email}${contact.phoneNumber.map(p => s"<br>Contact phone number: $p").getOrElse("")}<br><br>Message: ${contact.message}")
        _ <- emailAlgebra.receiveEmail(contact.email, contact.name, receivedSubject, receivedContent)
        subject = EmailSubject("Thanks for contacting me")
        content = EmailContent(s"Hi ${contact.name}, I received your message!")
        _ <- emailAlgebra.sendEmail(contact.email, subject, content)
        resp <- Ok()
      } yield resp
  }
}
