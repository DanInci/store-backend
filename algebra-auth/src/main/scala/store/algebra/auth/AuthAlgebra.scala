package store.algebra.auth

import store.effects.Async
import tsec.mac.jca.{HMACSHA256, MacSigningKey}

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
trait AuthAlgebra[F[_]] {

  def authenticate(username: String, password: String): F[AuthenticationToken]

  def authenticate(token: AuthenticationToken): F[Unit]

}

object AuthAlgebra {

  def async[F[_]: Async](authConfig: AuthConfig,
                         jwtKey: MacSigningKey[HMACSHA256]): AuthAlgebra[F] =
    new impl.AsyncAlgebraImpl[F](authConfig, jwtKey)

}
