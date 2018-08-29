package store.algebra.content.impl

import java.io.ByteArrayInputStream

import cats.implicits._
import com.amazonaws._
import com.amazonaws.auth._
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import store.algebra.content._
import store.core.{BlockingAlgebra, Link}
import store.effects.Async

import scala.collection.JavaConverters
import scala.util.Random
import scala.util.control.NonFatal

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 24/08/2018
  */
final class S3StorageAlgebra[F[_]](config: S3StorageConfig)(
    implicit
    val F: Async[F],
    val contentCtx: ContentContext[F]
) extends ContentStorageAlgebra[F]
    with BlockingAlgebra[F] {

  private lazy val logger = Slf4jLogger.unsafeCreate[F]

  private lazy val _s3Client = F.delay {
    val credentialsProvider = new AWSCredentialsProvider {
      override def getCredentials: AWSCredentials = new BasicAWSCredentials(
        config.accessKeyId,
        config.secretAccessKey
      )
      override def refresh(): Unit = ()
    }

    AmazonS3ClientBuilder
      .standard()
      .withCredentials(credentialsProvider)
      .withRegion(config.region)
      .build()
  }

  override def getContentLink(id: ContentID): F[Link] = F.delay {
    Link(config.baseLink + "/" + config.bucketName + "/"+ id)
  }

  override def getContent(id: ContentID): F[BinaryContent] = block {
    for {
      client <- _s3Client
      request = new GetObjectRequest(config.bucketName, id)
      s3object <- F
        .delay(client.getObject(request))
        .handleErrorWith(amazonServiceErrorHandler)
      content = BinaryContent(
        Stream
          .continually(s3object.getObjectContent.read)
          .takeWhile(_ != -1)
          .map(_.toByte)
          .toArray)
    } yield content
  }

  override def saveContent(path: Path,
                           format: Format,
                           content: BinaryContent): F[ContentID] = block {
    for {
      client <- _s3Client
      contentId = ContentID(
        path + Random.alphanumeric.take(25).mkString + "." + format)
      inputStream = new ByteArrayInputStream(content)
      metaData = {
        val x = new ObjectMetadata()
        x.setContentLength(content.length)
        x
      }
      request = new PutObjectRequest(
        config.bucketName,
        contentId,
        inputStream,
        metaData).withCannedAcl(CannedAccessControlList.PublicRead)
      _ <- F
        .delay(client.putObject(request))
        .handleErrorWith(amazonServiceErrorHandler)
        .void
    } yield contentId
  }

  override def removeContent(id: ContentID): F[Unit] = block {
    for {
      client <- _s3Client
      request = new DeleteObjectRequest(config.bucketName, id)
      _ <- F
        .delay(client.deleteObject(request))
        .handleErrorWith(amazonServiceErrorHandler)
        .void
    } yield ()
  }

  override def removeContentsFromPath(path: Path): F[Unit] = block {
    for {
      client <- _s3Client
      listRequest = new ListObjectsRequest()
        .withBucketName(config.bucketName)
        .withPrefix(path)
      listResult <- F
        .delay(client.listObjects(listRequest))
        .handleErrorWith(amazonServiceErrorHandler)
      deleteRequest = new DeleteObjectsRequest(config.bucketName)
        .withKeys(
          JavaConverters
            .asScalaIterator(listResult.getObjectSummaries.iterator())
            .toSeq
            .map(_.getKey): _*)
      _ <- F
        .delay(client.deleteObjects(deleteRequest))
        .handleErrorWith(amazonServiceErrorHandler)
        .void
    } yield ()
  }

  private def amazonServiceErrorHandler[A](e: Throwable): F[A] = e match {
    case NonFatal(e: AmazonClientException) =>
      logger
        .error(s"Amazon client exception: ${e.getMessage}")
        .flatMap(_ => F.raiseError[A](e))
    case NonFatal(e: AmazonServiceException) =>
      logger
        .error(s"Amazon client exception: ${e.getMessage}")
        .flatMap(_ => F.raiseError[A](e))
    case ex => F.raiseError[A](ex)
  }

}

object S3StorageAlgebra {

  def async[F[_]: Async](s3Config: S3StorageConfig)(
      implicit contentCtx: ContentContext[F]) =
    new S3StorageAlgebra[F](s3Config)

}
