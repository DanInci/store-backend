package store.service.order

import cats.data.NonEmptyList
import org.http4s.HttpService
import store.algebra.email.ModuleEmailConcurrent
import store.algebra.order.ModuleOrderAsync
import store.service.order.rest.{ContactRestService, OrderRestService}

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait ModuleOrderServiceAsync[F[_]] {
  this: ModuleOrderAsync[F] with ModuleEmailConcurrent[F] =>

  def orderModuleService: HttpService[F] = _service

  def orderRestService: OrderRestService[F] = _orderRestService

  def contactRestService: ContactRestService[F] = _contactRestService

  private lazy val _orderRestService: OrderRestService[F] =
    new OrderRestService[F](
      orderAlgebra = orderAlgebra,
      emailAlgebra = emailAlgebra
    )

  private lazy val _contactRestService: ContactRestService[F] =
    new ContactRestService[F](
      emailAlgebra = emailAlgebra
    )

  private lazy val _service: HttpService[F] = {
    import cats.implicits._
    NonEmptyList
      .of(
        orderRestService.service,
        contactRestService.service
      )
      .reduceK
  }

}
