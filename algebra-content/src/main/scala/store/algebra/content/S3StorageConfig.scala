package store.algebra.content

import cats.effect.Sync
import store.config.ConfigLoader

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 24/08/2018
  */
final case class S3StorageConfig(
    accessKeyId: String,
    secretAccessKey: String,
    bucketName: String,
    region: String,
    baseLink: String
)

object S3StorageConfig extends ConfigLoader[S3StorageConfig] {
  override def default[F[_]: Sync]: F[S3StorageConfig] = this.load("store.s3")
}
