package store.db

import fs2.Stream
import io.chrisdavenport.linebacker.DualContext
import io.chrisdavenport.linebacker.contexts.Executors
import store.effects._

import scala.concurrent.ExecutionContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait DatabaseContext[F[_]] extends DualContext[F]

object DatabaseContext {

  def create[F[_]: Sync: Async](connectionPoolSize: Int)(implicit ec: ExecutionContext): Stream[F, DatabaseContext[F]] =
    Executors
      .fixedPool[F](connectionPoolSize)
      .map(blockingExecutor =>
        new DatabaseContext[F] {
          override def blockingContext: ExecutionContext = ec

          override def defaultContext: ExecutionContext = ExecutionContext.fromExecutorService(blockingExecutor)
      })
}
