package store.algebra.auth.impl

import cats.implicits._
import busymachines.core.UnauthorizedFailure
import store.algebra.auth._
import store.effects.Async
import tsec.jws.mac.JWTMac
import tsec.jwt.JWTClaims
import tsec.mac.jca.{HMACSHA256, MacSigningKey}

import scala.concurrent.duration._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
final class AsyncAlgebraImpl[F[_]](
    private val authConfig: AuthConfig,
    private val jwtKey: MacSigningKey[HMACSHA256]
)(
    implicit val F: Async[F]
) extends AuthAlgebra[F] {

  override def authenticate(username: String,
                            password: String): F[AuthenticationToken] = {
    authConfig.users.find(auth =>
      auth.username == username && auth.password == password) match {
      case Some(_) => generateJWT()
      case None    => F.raiseError(UnauthorizedFailure("Login attempt failed"))
    }
  }

  override def authenticate(token: AuthenticationToken): F[Unit] =
    for {
      _ <- JWTMac
        .verifyAndParse[F, HMACSHA256](token, jwtKey)
        .handleErrorWith(e => F.raiseError(UnauthorizedFailure(e.getMessage)))
    } yield ()

  private def generateJWT(): F[AuthenticationToken] =
    for {
      claims <- JWTClaims.withDuration[F](
        expiration = Some(30.seconds)
      )
      jwt <- JWTMac.buildToString[F, HMACSHA256](claims, jwtKey)
    } yield AuthenticationToken(jwt)

}
