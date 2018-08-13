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

  def filesAlgebra: ContentStorageAlgebra[F] = _moduleFiles

  private lazy val _moduleFiles: ContentStorageAlgebra[F] = new impl.FileStorageAlgebra[F](fileStorageConfig)
}
