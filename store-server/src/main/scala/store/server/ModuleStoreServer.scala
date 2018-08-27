package store.server

import doobie.util.transactor.Transactor
import org.http4s.HttpService
import org.http4s.server.middleware.CORS
import store.algebra.content._
import store.algebra.email._
import store.algebra.order._
import store.algebra.product._
import store.db.DatabaseContext
import store.effects._
import store.service.order._
import store.service.product._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ModuleStoreServer[F[_]]
    extends ModuleProductServiceAsync[F]
    with ModuleOrderServiceAsync[F]
    with ModuleProductAsync[F]
    with ModuleContentAsync[F]
    with ModuleOrderAsync[F]
    with ModuleEmailAsync[F] {

  override implicit def async: Async[F]

  override implicit def transactor: Transactor[F]

  override implicit def dbContext: DatabaseContext[F]

  override implicit def contentContext: ContentContext[F]

  override def fileStorageConfig: FileStorageConfig

  override def s3StorageConfig: S3StorageConfig

  override def emailConfig: EmailConfig

  def storeServerService: HttpService[F] = CORS {
    import cats.implicits._
    NonEmptyList
      .of(
        productModuleService,
        orderModuleService
      )
      .reduceK
  }

}

object ModuleStoreServer {

  def concurrent[F[_]](filesConfig: FileStorageConfig, s3Config: S3StorageConfig, eConfig: EmailConfig)(
      implicit c: Concurrent[F],
      t: Transactor[F],
      dbc: DatabaseContext[F],
      cc: ContentContext[F]): ModuleStoreServer[F] =
    new ModuleStoreServer[F] {
      override implicit def async: Async[F] = c

      override implicit def transactor: Transactor[F] = t

      override implicit def dbContext: DatabaseContext[F] = dbc

      override implicit def contentContext: ContentContext[F] = cc

      override def fileStorageConfig: FileStorageConfig = filesConfig

      override def s3StorageConfig: S3StorageConfig = s3Config

      override def emailConfig: EmailConfig = eConfig
    }

}
