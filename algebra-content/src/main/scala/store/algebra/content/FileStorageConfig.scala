package store.algebra.content

import cats.effect.Sync
import store.config.ConfigLoader

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
final case class FileStorageConfig(
    imagesFolder: String
)

object FileStorageConfig extends ConfigLoader[FileStorageConfig] {
  override def default[F[_]: Sync]: F[FileStorageConfig] = this.load("algebra.files")
}
