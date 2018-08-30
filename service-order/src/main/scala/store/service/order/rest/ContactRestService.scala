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
) extends Http4sDsl[F]
    with ErrorHandlingInstances[F]
    with OrderServiceJSON {

  val service: HttpService[F] = HttpServiceWithErrorHandling {
    case req @ POST -> Root / "contact" =>
      for {
        contact <- req.as[ContactRequest]
        subject = Subject(s"${contact.name} sent you an email")
        content = Content(s"Contact email: ${contact.email}<br>Contact phone: ${contact.phoneNumber}<br><br>Message: ${contact.message}")
        _ <- emailAlgebra.receiveMail(contact.email, subject, content)
        resp <- Ok()
      } yield resp
  }
}
