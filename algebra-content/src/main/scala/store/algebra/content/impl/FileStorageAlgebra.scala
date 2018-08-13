package store.algebra.content.impl

import better.files.File
import better.files.File.currentWorkingDirectory
import cats.effect.Async
import store.algebra.content._
import store.core.ProductID
import store.db.BlockingAlgebra

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
final class FileStorageAlgebra[F[_]](filesConfig: FileStorageConfig)(
    implicit
    val F: Async[F],
    val contentCtx: ContentContext[F]
) extends ContentStorageAlgebra[F]
    with BlockingAlgebra[F] {

  override def saveContent(content: BinaryContent,
                           productId: ProductID): F[ContentID] =
    F.delay {
      val parentDirectory =
        (currentWorkingDirectory / filesConfig.imagesFolder / "product" / s"$productId")
          .createDirectoryIfNotExists(createParents = true)
      val file = File.newTemporaryFile("image_", "", Some(parentDirectory))
      writeContentToFile(content, file)
      ContentID(currentWorkingDirectory.relativize(file).toString)
    }

  private def writeContentToFile(content: BinaryContent, file: File): F[File] =
    block {
      F.delay(file.writeByteArray(content))
    }

  override def getContent(id: ContentID): F[BinaryContent] = {
    val file = File(id)
    getContentFromFile(file)
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
