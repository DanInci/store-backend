package store.algebra.email

import cats.effect.Sync
import store.config.ConfigLoader

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
final case class EmailConfig(
    from: String,
    user: String,
    password: String,
    host: String,
    port: Int,
    auth: Boolean,
    startTLS: Boolean
)

object EmailConfig extends ConfigLoader[EmailConfig] {
  override def default[F[_] : Sync]: F[EmailConfig] =
    this.load("store.email")
}
