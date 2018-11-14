package store.server

import store.effects._
import fs2.Stream
import store.db.config._
import store.db._
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import monix.execution.Scheduler
import store.algebra.content.{ContentContext, FileStorageConfig, S3StorageConfig}
import store.algebra.email.{EmailConfig, EmailContext}

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
      serverConfig   <- Stream.eval(StoreServerConfig.default[F])
      dbConfig       <- Stream.eval(DatabaseConfig.default[F])
      filesConfig    <- Stream.eval(FileStorageConfig.default[F])
      s3Config       <- Stream.eval(S3StorageConfig.default)
      emailConfig    <- Stream.eval(EmailConfig.default)
      transactor     <- Stream.eval(DatabaseConfigAlgebra.transactor[F](dbConfig))
      nrOfMigs       <- Stream.eval(DatabaseConfigAlgebra.initializeSQLDb[F](dbConfig))
      _              <- Stream.eval(logger.info(s"Successfully ran $nrOfMigs migration(s)"))
      dbContext      <- DatabaseContext.create[F](dbConfig.connectionPoolSize)
      contentContext <- ContentContext.create[F]
      emailContext   <- EmailContext.create[F]
      storeModule    <- Stream.eval(moduleInit(transactor, dbContext, filesConfig, s3Config, contentContext, emailConfig, emailContext))
      _              <- Stream.eval(logger.info("Successfully initialized store-server"))
      _              <- Stream.eval(logger.info(s"Started server on ${serverConfig.host}:${serverConfig.port}"))
    } yield (serverConfig, storeModule)

  private def moduleInit(transactor: Transactor[F], dbContext: DatabaseContext[F], filesConfig: FileStorageConfig, s3Config: S3StorageConfig, contentContext: ContentContext[F], emailConfig: EmailConfig, emailContext: EmailContext[F]): F[ModuleStoreServer[F]] =
    Concurrent.apply[F].delay(ModuleStoreServer.concurrent(filesConfig, s3Config, emailConfig)(implicitly, transactor, dbContext, contentContext, emailContext))

}

object StoreServer {

  def concurrent[F[_]: Concurrent](implicit scheduler: Scheduler, logger: SelfAwareStructuredLogger[F]): Stream[F, StoreServer[F]] =
    Stream.eval(Concurrent.apply[F].delay(new StoreServer[F]))

}
