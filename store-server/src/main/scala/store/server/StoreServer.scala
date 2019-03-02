package store.server

import store.effects._
import fs2.Stream
import store.db.config._
import store.db._
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import monix.execution.Scheduler
import store.algebra.auth.AuthConfig
import store.algebra.content._
import store.algebra.email._
import tsec.mac.jca._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
class StoreServer[F[_]: Concurrent] private (
    implicit
    val scheduler: Scheduler,
    val logger: SelfAwareStructuredLogger[F]
) {

  def init: Stream[F, (StoreServerConfig, ModuleStoreServer[F])] =
    for {
      serverConfig <- Stream.eval(StoreServerConfig.default[F])
      _ <- Stream.eval(logger.info(serverConfig.mode))
      dbConfig <- if (serverConfig.mode.toUpperCase() == "development") {
        Stream.eval(DatabaseConfig.development[F])
      } else {
        Stream.eval(DatabaseConfig.default[F])
      }
      filesConfig <- Stream.eval(FileStorageConfig.default[F])
      s3Config <- Stream.eval(S3StorageConfig.default)
      authConfig <- Stream.eval(AuthConfig.default[F])
      jwtKey <- Stream.eval(HMACSHA256.generateKey[F])
      emailConfig <- Stream.eval(EmailConfig.default)
      transactor <- Stream.eval(DatabaseConfigAlgebra.transactor[F](dbConfig))
      nrOfMigs <- Stream.eval(
        DatabaseConfigAlgebra.initializeSQLDb[F](dbConfig))
      _ <- Stream.eval(logger.info(s"Successfully ran $nrOfMigs migration(s)"))
      dbContext <- DatabaseContext.create[F](dbConfig.connectionPoolSize)
      contentContext <- ContentContext.create[F]
      emailContext <- EmailContext.create[F]
      storeModule <- Stream.eval(
        moduleInit(transactor,
                   dbContext,
                   filesConfig,
                   s3Config,
                   contentContext,
                   authConfig,
                   jwtKey,
                   emailConfig,
                   emailContext))
      _ <- Stream.eval(logger.info(
        s"Successfully initialized store-server in ${serverConfig.mode.toUpperCase} mode"))
      _ <- Stream.eval(
        logger.info(
          s"Started server on ${serverConfig.host}:${serverConfig.port}"))
    } yield (serverConfig, storeModule)

  private def moduleInit(
      transactor: Transactor[F],
      dbContext: DatabaseContext[F],
      filesConfig: FileStorageConfig,
      s3Config: S3StorageConfig,
      contentContext: ContentContext[F],
      authConfig: AuthConfig,
      key: MacSigningKey[HMACSHA256],
      emailConfig: EmailConfig,
      emailContext: EmailContext[F]): F[ModuleStoreServer[F]] =
    Concurrent
      .apply[F]
      .delay(
        ModuleStoreServer
          .concurrent(filesConfig, s3Config, authConfig, key, emailConfig)(
            implicitly,
            transactor,
            dbContext,
            contentContext,
            emailContext))

}

object StoreServer {

  def concurrent[F[_]: Concurrent](
      implicit scheduler: Scheduler,
      logger: SelfAwareStructuredLogger[F]): Stream[F, StoreServer[F]] =
    Stream.eval(Concurrent.apply[F].delay(new StoreServer[F]))

}
