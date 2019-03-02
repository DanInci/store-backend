package store.service.user.rest

import java.nio.charset.StandardCharsets
import java.util.Base64

import busymachines.core.UnauthorizedFailure
import cats.implicits._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import store.algebra.auth.AuthAlgebra
import org.http4s.headers.Authorization
import store.effects._
import store.http._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
final class UserAuthRestService[F[_]](
    authAlgebra: AuthAlgebra[F]
)(
    implicit F: Async[F]
) extends Http4sDsl[F]
    with UserServiceJSON {

  private val userService: HttpService[F] = HttpService[F] {
    case req @ POST -> Root / "login" =>
      for {
        authHeader <- req.headers.get(Authorization) match {
          case Some(authHeader) => F.pure(authHeader.value)
          case None =>
            F.raiseError[String](
              UnauthorizedFailure(s"No Authorization Header provided"))
        }
        (username, password) <- decodeBasicAuthToken(authHeader) match {
          case Some((username, password)) => F.pure((username, password))
          case None =>
            F.raiseError(UnauthorizedFailure(s"Invalid Authorization Header"))
        }
        token <- authAlgebra.authenticate(username, password)
        resp <- Ok(token)
      } yield resp
  }

  private def decodeBasicAuthToken(
      authToken: String): Option[(String, String)] = {
    try {
      val base64Credentials = authToken.substring("Basic".length).trim
      val decodedBytes = Base64.getDecoder.decode(base64Credentials)
      val credentials = new String(decodedBytes, StandardCharsets.UTF_8)
      val splitted = credentials.split(":", 2)
      Some((splitted(0), splitted(1)))
    } catch {
      case _: Exception => Option.empty
    }
  }

  val service: HttpService[F] = userService

}
