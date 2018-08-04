package store.server

import store.effects._
import cats.effect.{Concurrent, IO}
import fs2.StreamApp
import fs2.Stream
import monix.execution.Scheduler
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
object StoreServerApp extends StreamApp[IO] {

  override def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    implicit val scheduler: Scheduler = Scheduler.global

    for {
      storeServer <- StoreServer.concurrent[IO]
      (serverConfig, serverModule) <- storeServer.init
      exitCode <- streamServer[IO](
        config = serverConfig,
        service = serverModule.storeServerService
      )
    } yield exitCode
  }

  def streamServer[F[_]: Effect: Concurrent](
      config: StoreServerConfig,
      service: HttpService[F]
  )(implicit ec: ExecutionContext): Stream[F, StreamApp.ExitCode] =
    BlazeBuilder[F]
      .bindHttp(config.port, config.host)
      .mountService(service, config.apiRoot)
      .serve

}
