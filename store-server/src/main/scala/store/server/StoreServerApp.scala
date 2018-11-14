package store.server

import store.effects._
import cats.effect.{Concurrent, IO}
import fs2.StreamApp
import fs2.Stream
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import monix.execution.Scheduler
import org.http4s.HttpService
import org.http4s.server.ServiceErrorHandler
import org.http4s.server.blaze.BlazeBuilder
import store.http.StoreErrorHandler

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
object StoreServerApp extends StreamApp[IO] {

  implicit val scheduler: Scheduler = Scheduler.global
  implicit val logger: SelfAwareStructuredLogger[IO] =
    Slf4jLogger.unsafeCreate[IO]

  override def stream(
      args: List[String],
      requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    for {
      storeServer <- StoreServer.concurrent[IO]
      (serverConfig, serverModule) <- storeServer.init
      exitCode <- streamServer[IO](
        config = serverConfig,
        service = serverModule.storeServerService,
        errorHandling = StoreErrorHandler.apply[IO]
      )
    } yield exitCode
  }

  def streamServer[F[_]: Effect: Concurrent](
      config: StoreServerConfig,
      service: HttpService[F],
      errorHandling: ServiceErrorHandler[F]
  ): Stream[F, StreamApp.ExitCode] =
    BlazeBuilder[F]
      .bindHttp(config.port, config.host)
      .mountService(service, config.apiRoot)
      .withServiceErrorHandler(errorHandling)
      .serve

}
