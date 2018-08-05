package store.algebra.files

import cats.effect.Sync
import store.config.ConfigLoader

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
final case class FilesAlgebraConfig(
    imagesFolder: String,
    productsFolder: String,
)

object FilesAlgebraConfig extends ConfigLoader[FilesAlgebraConfig] {
  override def default[F[_]: Sync]: F[FilesAlgebraConfig] = this.load("algebra.files")
}
