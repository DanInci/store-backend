package store.algebra.files.impl

import better.files.File
import store.algebra.files.entity.ImageFile
import store.algebra.files._
import cats.implicits._
import store.core.ProductID
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
final private[files] class AsyncAlgebraImpl[F[_]](config: FilesAlgebraConfig)(
    implicit
    val F: Async[F],
    val filesContext: FilesContext[F]
) extends FilesAlgebra[F] {

  override def retrieveImagesForProduct(productId: ProductID): F[List[ImageFile]] = {
    val parentDirectory = File(s"${config.imagesFolder}/${config.productsFolder}/$productId")
    val files = parentDirectory.collectChildren(_ => true).toList
    files.map(f => retrieveImageForFile(f)).sequence
  }

  override def saveImagesForProduct(productId: ProductID, images: List[ImageFile]): F[Unit] = F.delay {
    images.map(saveImageForProduct(productId, _))
  }

  private def retrieveImageForFile(file: File): F[ImageFile] = {
    val prefix = file.name.split("_").headOption.getOrElse("")
    val name = prefix.replace("+"," ")
    getContentFromFile(file).map(c => ImageFile(name, c))
  }

  private def saveImageForProduct(productId: ProductID, image: ImageFile): F[Unit] = F.delay {
    for {
      file <- createNewImageFileForProduct(productId, image.name)
      _ <- writeContentToFile(image.content, file)
    } yield ()
  }

  private def createNewImageFileForProduct(productId: ProductID, name: String): F[File] = F.delay {
    val formattedName = name.replace(" ", "+")
    val parentDirectory = File(s"${config.imagesFolder}/${config.productsFolder}/$productId").createDirectoryIfNotExists(createParents = true)
    val file = File.newTemporaryDirectory(s"${formattedName}_", Some(parentDirectory))
    file
  }

  private def writeContentToFile(content: BinaryContent, file: File): F[File] = {
    shift(F.delay(file.writeByteArray(content)))
  }

  private def getContentFromFile(file: File): F[BinaryContent] = {
    shift(F.delay(BinaryContent(file.byteArray)))
  }

}
