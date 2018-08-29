package store.algebra.content.impl

import better.files.File
import better.files.File.currentWorkingDirectory
import cats.effect.Async
import store.algebra.content._
import store.core.{BlockingAlgebra, Link}

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
final class FileStorageAlgebra[F[_]](config: FileStorageConfig)(
    implicit
    val F: Async[F],
    val contentCtx: ContentContext[F]
) extends ContentStorageAlgebra[F]
    with BlockingAlgebra[F] {

  override def getContentLink(id: ContentID): F[Link] = F.delay{
    Link(config.baseLink + "/" + id)
  }

  override def saveContent(path: Path, format: Format, content: BinaryContent): F[ContentID] =  F.delay {
    val parentDirectory =
      (currentWorkingDirectory / config.imagesFolder / path)
        .createDirectoryIfNotExists(createParents = true)
    val file = File.newTemporaryFile("image_", "", Some(parentDirectory))
    file.writeByteArray(content)
    ContentID(currentWorkingDirectory.relativize(file).toString)
  }

  override def getContent(id: ContentID): F[BinaryContent] = {
    val file = File(id)
    getContentFromFile(file)
  }

  override def removeContent(id: ContentID): F[Unit] = F.delay {
    val file = currentWorkingDirectory / config.imagesFolder / id
    if(file.isDirectory) file.delete(true)
  }

  override def removeContentsFromPath(path: Path): F[Unit] = F.delay {
    val productDirectory = currentWorkingDirectory / config.imagesFolder / path
    productDirectory.delete(true)
  }

  private def getContentFromFile(file: File): F[BinaryContent] = block {
    F.delay(BinaryContent(file.byteArray))
  }
}

object FileStorageAlgebra {

  def async[F[_]: Async](filesConfig: FileStorageConfig)(
      implicit contentCtx: ContentContext[F]) =
    new FileStorageAlgebra[F](filesConfig)

}
