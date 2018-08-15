package store.algebra.content

import store.core.ProductID
/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
trait ContentStorageAlgebra[F[_]] {

  def getContent(contentId: ContentID): F[BinaryContent]

  def removeContentForProduct(productID: ProductID): F[Unit]

  def saveContent(binaryContent: BinaryContent, productId: ProductID): F[ContentID]

}
