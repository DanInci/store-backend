package store.algebra.content

import store.core.ProductID
/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
trait ContentStorageAlgebra[F[_]] {

  def getContent(contentId: ContentID): F[BinaryContent]

  def saveContent(binaryContent: BinaryContent, productId: ProductID): F[ContentID]

}
