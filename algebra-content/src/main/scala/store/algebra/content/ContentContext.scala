package store.algebra.content

import fs2.Stream
import io.chrisdavenport.linebacker.DualContext
import io.chrisdavenport.linebacker.contexts.Executors
import store.effects.{Async, Sync}

import scala.concurrent.ExecutionContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
trait ContentContext[F[_]] extends DualContext[F]

object ContentContext {

  def create[F[_]: Sync: Async](implicit ec: ExecutionContext): Stream[F, ContentContext[F]] =
    Executors
      .unbound[F]
      .map(blockingExecutor =>
        new ContentContext[F] {
          override def defaultContext: ExecutionContext = ec
          override def blockingContext: ExecutionContext = ExecutionContext.fromExecutorService(blockingExecutor)
        })
}
