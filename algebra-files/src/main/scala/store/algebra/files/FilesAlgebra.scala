package store.algebra.files

import store.algebra.files.entity.ImageFile
import store.core.ProductID
import store.effects.Async

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
trait FilesAlgebra[F[_]] {

  protected def shift[M](thunk: => F[M])(implicit F: Async[F], filesContext: FilesContext[F]): F[M] = {
    filesContext.block(thunk)
  }

  def retrieveImagesForProduct(productId: ProductID): F[List[ImageFile]]

  def saveImagesForProduct(productId: ProductID, images: List[ImageFile]): F[Unit]

}

object FilesAlgebra {
  import store.effects._

  def async[F[_]: Async](config: FilesAlgebraConfig)(implicit filesContext: FilesContext[F]): FilesAlgebra[F] = new impl.AsyncAlgebraImpl[F](config)
}
