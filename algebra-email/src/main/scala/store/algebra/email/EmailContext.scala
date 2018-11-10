package store.algebra.email

import fs2.Stream
import io.chrisdavenport.linebacker.DualContext
import io.chrisdavenport.linebacker.contexts.Executors
import store.effects.{Async, Sync}

import scala.concurrent.ExecutionContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 29/10/2018
  */
trait EmailContext[F[_]] extends DualContext[F]

object EmailContext {

  def create[F[_]: Sync: Async](
      implicit ec: ExecutionContext): Stream[F, EmailContext[F]] =
    Executors
      .unbound[F]
      .map(blockingExecutor =>
        new EmailContext[F] {
          override def defaultContext: ExecutionContext = ec
          override def blockingContext: ExecutionContext = ExecutionContext.fromExecutorService(blockingExecutor)
      })
}
