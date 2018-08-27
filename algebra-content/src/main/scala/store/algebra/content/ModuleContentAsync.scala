package store.algebra.content

import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
trait ModuleContentAsync[F[_]] {

  implicit def async: Async[F]

  implicit def contentContext: ContentContext[F]

  def fileStorageConfig: FileStorageConfig

  def s3StorageConfig: S3StorageConfig

  def filesStorageAlgebra: ContentStorageAlgebra[F] = _filesStorageAlgebra

  def s3StorageAlgebra: ContentStorageAlgebra[F] = _s3StorageAlgebra

  private lazy val _filesStorageAlgebra: ContentStorageAlgebra[F] =
    new impl.FileStorageAlgebra[F](fileStorageConfig)

  private lazy val _s3StorageAlgebra: ContentStorageAlgebra[F] =
    new impl.S3StorageAlgebra[F](s3StorageConfig)
}
