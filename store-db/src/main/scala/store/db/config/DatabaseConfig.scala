package store.db.config

import cats.effect.Sync
import store.config.ConfigLoader

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
final case class DatabaseConfig(
    driver: String,
    url: String,
    user: String,
    password: String,
    clean: Boolean,
    locations: List[String],
    connectionPoolSize: Int
)

object DatabaseConfig extends ConfigLoader[DatabaseConfig] {
  override def default[F[_]: Sync]: F[DatabaseConfig] =
    this.load[F]("store.db")

  def testing[F[_]: Sync]: F[DatabaseConfig] =
    this.load[F]("store.test")
}
