package store.algebra.files

import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
trait ModuleFilesAsync[F[_]] {

  implicit def async: Async[F]

  implicit def filesContext: FilesContext[F]

  def filesConfig: FilesAlgebraConfig

  def filesAlgebra: FilesAlgebra[F] = _moduleFiles

  private lazy val _moduleFiles: FilesAlgebra[F] = new impl.AsyncAlgebraImpl[F](filesConfig)


}
