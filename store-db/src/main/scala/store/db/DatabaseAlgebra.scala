package store.db

import cats.implicits._
import busymachines.core.{AnomalousFailure, ConflictFailure}
import doobie._
import doobie.implicits._
import store.effects._
import store.core.BlockingAlgebra

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 09/11/2018
  */
trait DatabaseAlgebra[F[_]] { this: BlockingAlgebra[F] =>

  private val UNIQUE_VIOLATION = SqlState("23505")

  protected def transact[A](
      query: ConnectionIO[A]
  )(implicit F: Async[F],
    transactor: Transactor[F],
    context: DatabaseContext[F]): F[A] = {
    block(
      handleSqlStateErrors(query).transact(transactor).flatMap {
        case Left(anomaly) => F.raiseError[A](anomaly)
        case Right(x)      => F.pure[A](x)
      }
    )
  }

  protected def pure[A](value: A): ConnectionIO[A] =
    AsyncConnectionIO.pure(value)
  protected def raiseError[A](error: Throwable): ConnectionIO[A] =
    AsyncConnectionIO.raiseError(error)

  private def handleSqlStateErrors[A](
      query: ConnectionIO[A]): ConnectionIO[Either[AnomalousFailure, A]] =
    query.attemptSomeSqlState {
      case UNIQUE_VIOLATION =>
        ConflictFailure("There was a conflict. Please try again")
    }
}
