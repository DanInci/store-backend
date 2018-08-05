package store.algebra.files

import fs2.Stream
import io.chrisdavenport.linebacker.DualContext
import io.chrisdavenport.linebacker.contexts.Executors
import store.effects.{Async, Sync}

import scala.concurrent.ExecutionContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
trait FilesContext[F[_]] extends DualContext[F]

object FilesContext {

  def create[F[_]: Sync: Async](implicit ec: ExecutionContext): Stream[F, FilesContext[F]] =
    Executors
      .unbound[F]
      .map(blockingExecutor =>
        new FilesContext[F] {
          override def blockingContext: ExecutionContext = ec

          override def defaultContext: ExecutionContext = ExecutionContext.fromExecutorService(blockingExecutor)
        })
}
