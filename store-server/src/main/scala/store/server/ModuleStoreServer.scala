package store.server

import cats.data.NonEmptyList
import cats.effect.Concurrent
import doobie.util.transactor.Transactor
import org.http4s.HttpService
import store.algebra.product.ModuleProductAsync
import store.db.DatabaseContext
import store.effects.Async
import store.service.product.ModuleProductServiceAsync

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ModuleStoreServer[F[_]] extends ModuleProductServiceAsync[F] with ModuleProductAsync[F]{

  override implicit def async: Async[F]

  override implicit def transactor: Transactor[F]

  override implicit def dbContext: DatabaseContext[F]

  def storeServerService: HttpService[F] = {
    import cats.implicits._
    NonEmptyList
      .of(
        productRestService.service
      )
      .reduceK
  }

}

object ModuleStoreServer {

  def concurrent[F[_]](implicit c: Concurrent[F], t: Transactor[F], dbc: DatabaseContext[F]): ModuleStoreServer[F] =
    new ModuleStoreServer[F] {
      override implicit def async: Async[F] = c

      override implicit def transactor: Transactor[F] = t

      override implicit def dbContext: DatabaseContext[F] = dbc
    }

}
