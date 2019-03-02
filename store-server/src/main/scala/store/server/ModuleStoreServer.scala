package store.server

import doobie.util.transactor.Transactor
import org.http4s.HttpService
import org.http4s.server.middleware.CORS
import store.algebra.auth._
import store.algebra.content._
import store.algebra.email._
import store.algebra.httpsec._
import store.algebra.order._
import store.algebra.product._
import store.db.DatabaseContext
import store.effects._
import store.service.order._
import store.service.product._
import store.service.user._
import tsec.mac.jca.{HMACSHA256, MacSigningKey}

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ModuleStoreServer[F[_]]
    extends ModuleUserServiceAsync[F]
    with ModuleProductServiceAsync[F]
    with ModuleOrderServiceAsync[F]
    with ModuleProductAsync[F]
    with ModuleContentAsync[F]
    with ModuleOrderAsync[F]
    with ModuleAuthAsync[F]
    with ModuleEmailConcurrent[F] {

  override implicit def concurrent: Concurrent[F]

  override implicit def async: Async[F] = concurrent

  override implicit def transactor: Transactor[F]

  override implicit def dbContext: DatabaseContext[F]

  override implicit def contentContext: ContentContext[F]

  override implicit def emailContext: EmailContext[F]

  override def fileStorageConfig: FileStorageConfig

  override def s3StorageConfig: S3StorageConfig

  override def authConfig: AuthConfig

  override def jwtKey: MacSigningKey[HMACSHA256]

  override def emailConfig: EmailConfig

  def authCtxMiddleware: AuthCtxMiddleware[F] =
    AuthedHttp4s.userTokenAuthMiddleware[F](authAlgebra)

  /*_*/
  def storeServerService: HttpService[F] = CORS {
    import cats.implicits._
    val authedService: AuthCtxService[F] = NonEmptyList
      .of(
        productModuleAuthedService,
        orderModuleAuthedService
      )
      .reduceK
    val service: HttpService[F] = NonEmptyList
      .of(
        userModuleService,
        productModuleService,
        orderModuleService
      )
      .reduceK

    service <+> authCtxMiddleware(authedService)
  }
  /*_*/

}

object ModuleStoreServer {

  def concurrent[F[_]](filesConfig: FileStorageConfig,
                       s3Config: S3StorageConfig,
                       aConfig: AuthConfig,
                       key: MacSigningKey[HMACSHA256],
                       eConfig: EmailConfig)(
      implicit c: Concurrent[F],
      t: Transactor[F],
      dbc: DatabaseContext[F],
      cc: ContentContext[F],
      ec: EmailContext[F]): ModuleStoreServer[F] =
    new ModuleStoreServer[F] {
      override implicit def concurrent: Concurrent[F] = c

      override implicit def transactor: Transactor[F] = t

      override implicit def dbContext: DatabaseContext[F] = dbc

      override implicit def contentContext: ContentContext[F] = cc

      override implicit def emailContext: EmailContext[F] = ec

      override def fileStorageConfig: FileStorageConfig = filesConfig

      override def s3StorageConfig: S3StorageConfig = s3Config

      override def authConfig: AuthConfig = aConfig

      override def jwtKey: MacSigningKey[HMACSHA256] = key

      override def emailConfig: EmailConfig = eConfig
    }

}
