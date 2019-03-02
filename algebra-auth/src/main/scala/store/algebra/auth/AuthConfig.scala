package store.algebra.auth

import cats.effect.Sync
import store.config.ConfigLoader

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
case class AuthInfo(
    username: String,
    password: String
)

case class AuthConfig(
    users: List[AuthInfo]
)

object AuthConfig extends ConfigLoader[AuthConfig] {
  override def default[F[_]: Sync]: F[AuthConfig] =
    this.load("store.auth")
}
