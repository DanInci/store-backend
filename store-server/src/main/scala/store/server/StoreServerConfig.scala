package store.server

import cats.effect.Sync
import store.config.ConfigLoader

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
final case class StoreServerConfig(
    host: String,
    port: Int,
    apiRoot: String,
    mode: String
)

object StoreServerConfig extends ConfigLoader[StoreServerConfig] {
  override def default[F[_]: Sync]: F[StoreServerConfig] =
    this.load[F]("store.server")
}
