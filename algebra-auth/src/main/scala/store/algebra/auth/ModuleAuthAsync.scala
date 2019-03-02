package store.algebra.auth

import store.effects._
import tsec.mac.jca._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
trait ModuleAuthAsync[F[_]] {

  implicit def async: Async[F]

  def authConfig: AuthConfig

  def jwtKey: MacSigningKey[HMACSHA256]

  def authAlgebra: AuthAlgebra[F] = _authAlgebra

  private lazy val _authAlgebra = AuthAlgebra.async(
    authConfig = authConfig,
    jwtKey = jwtKey
  )

}
