package store.algebra.content.impl

import java.util.UUID

import cats.implicits._
import better.files.File
import better.files.File.currentWorkingDirectory
import cats.effect.Async
import store.algebra.content._
import store.algebra.content.entity.Format
import store.core._

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

  override def saveContent(path: Path, format: Format, content: BinaryContent): F[ContentID] = for {
    uuid <- F.delay(UUID.randomUUID)
    parentDirectory = currentWorkingDirectory / config.contentFolder / path
    file = File(parentDirectory / uuid.toString + "." + format)
    _ <- writeContentToFile(file, content)
    contentId = ContentID(currentWorkingDirectory.relativize(file).toString)
  } yield contentId

  override def getContent(id: ContentID): F[BinaryContent] = {
    val file = File(id)
    readContentFromFile(file)
  }

  override def removeContent(id: ContentID): F[Unit] = block {
    F.delay {
      val file = currentWorkingDirectory / id
      if (!file.isDirectory) file.delete(swallowIOExceptions = true)
    }
  }

  override def removeContentsFromPath(path: Path): F[Unit] = block {
    F.delay {
      val productDirectory = currentWorkingDirectory / config.contentFolder / path
      productDirectory.delete(swallowIOExceptions = true)
    }
  }

  private def readContentFromFile(file: File): F[BinaryContent] = block {
    F.delay(BinaryContent(file.byteArray))
  }

  private def writeContentToFile(file: File, content: BinaryContent): F[Unit] = block {
    file.createFileIfNotExists(createParents = true)
    F.delay(file.writeByteArray(content)).map(_ => ())
  }
}

object FileStorageAlgebra {

  def async[F[_]: Async](filesConfig: FileStorageConfig)(
      implicit contentCtx: ContentContext[F]) =
    new FileStorageAlgebra[F](filesConfig)

}
