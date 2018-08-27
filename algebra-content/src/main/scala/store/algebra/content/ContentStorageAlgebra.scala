package store.algebra.content

import store.core.Link

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
trait ContentStorageAlgebra[F[_]] {

  def getContentLink(id: ContentID): F[Link]

  def getContent(id: ContentID): F[BinaryContent]

  def saveContent(path: Path, format: Format, content: BinaryContent): F[ContentID]

  def removeContent(id: ContentID): F[Unit]

  def removeContentsFromPath(path: Path): F[Unit]

}
