package store.server

import store.effects._
import fs2.Stream
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import store.db.config._
import store.db._
import doobie.util.transactor.Transactor
import monix.execution.Scheduler
import store.algebra.content.{ContentContext, FileStorageConfig}
import store.algebra.email.EmailConfig

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
class StoreServer[F[_]: Concurrent] private (
    implicit scheduler: Scheduler
) {

  private val logger = Slf4jLogger.unsafeCreate[F]

  def init: Stream[F, (StoreServerConfig, ModuleStoreServer[F])] =
    for {
      serverConfig <- Stream.eval(StoreServerConfig.default[F])
      dbConfig     <- Stream.eval(DatabaseConfig.default[F])
      filesConfig  <- Stream.eval(FileStorageConfig.default[F])
      emailConfig  <- Stream.eval(EmailConfig.default)
      transactor   <- Stream.eval(DatabaseConfigAlgebra.transactor[F](dbConfig))
      nrOfMigs     <- Stream.eval(DatabaseConfigAlgebra.initializeSQLDb[F](dbConfig))
      _            <- Stream.eval(logger.info(s"Successfully ran $nrOfMigs migration(s)"))
      dbContext    <- DatabaseContext.create[F](dbConfig.connectionPoolSize)
      filesContext <- ContentContext.create[F]
      storeModule  <- Stream.eval(moduleInit(transactor, dbContext, filesConfig,filesContext, emailConfig))
      _            <- Stream.eval(logger.info("Successfully initialized store-server"))
      _            <- Stream.eval(logger.info(s"Started server on ${serverConfig.host}:${serverConfig.port}"))
    } yield (serverConfig, storeModule)

  private def moduleInit(transactor: Transactor[F], dbContext: DatabaseContext[F], filesConfig: FileStorageConfig, filesContext: ContentContext[F], emailConfig: EmailConfig): F[ModuleStoreServer[F]] =
    Concurrent.apply[F].delay(ModuleStoreServer.concurrent(filesConfig, emailConfig)(implicitly, transactor, dbContext, filesContext))

}

object StoreServer {

  def concurrent[F[_]: Concurrent](implicit scheduler: Scheduler): Stream[F, StoreServer[F]] =
    Stream.eval(Concurrent.apply[F].delay(new StoreServer[F]))

}
