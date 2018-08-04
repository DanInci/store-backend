package store

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
package object effects extends AnyRef {

  type NonEmptyList[A] = cats.data.NonEmptyList[A]
  @inline def NonEmptyList: cats.data.NonEmptyList.type = cats.data.NonEmptyList

  type Seq[A] = scala.collection.immutable.Seq[A]
  @inline def Seq: scala.collection.immutable.Seq.type = scala.collection.immutable.Seq

  type Sync[F[_]] = cats.effect.Sync[F]
  @inline def Sync: cats.effect.Sync.type = cats.effect.Sync

  type Async[F[_]] = cats.effect.Async[F]
  @inline def Async: cats.effect.Async.type = cats.effect.Async

  type Effect[F[_]] = cats.effect.Effect[F]
  @inline def Effect: cats.effect.Effect.type = cats.effect.Effect

  type Concurrent[F[_]] = cats.effect.Concurrent[F]
  @inline def Concurrent: cats.effect.Concurrent.type = cats.effect.Concurrent

  type Monad[F[_]] = cats.Monad[F]
  @inline def Monad: cats.Monad.type = cats.Monad

  type MonadError[F[_], E] = cats.MonadError[F, E]
  @inline def MonadError: cats.MonadError.type = cats.MonadError

  type Applicative[F[_]] = cats.Applicative[F]
  @inline def Applicative: cats.Applicative.type = cats.Applicative

}
