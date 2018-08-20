package store.service.order

import org.http4s.HttpService
import store.algebra.email.ModuleEmailAsync
import store.algebra.order.ModuleOrderAsync
import store.service.order.rest.OrderRestService

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait ModuleOrderServiceAsync[F[_]] {
  this: ModuleOrderAsync[F] with ModuleEmailAsync[F] =>

  def orderModuleService: HttpService[F] = _service

  def orderRestService: OrderRestService[F] = _orderRestService

  private lazy val _orderRestService: OrderRestService[F] =
    new OrderRestService[F](
      orderAlgebra = orderAlgebra,
      emailAlgebra = emailAlgebra
    )

  private lazy val _service: HttpService[F] = orderRestService.service

}
