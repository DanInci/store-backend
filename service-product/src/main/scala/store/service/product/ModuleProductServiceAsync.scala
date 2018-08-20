package store.service.product

import cats.data.NonEmptyList
import org.http4s.HttpService
import store.algebra.content.ModuleContentAsync
import store.algebra.product.ModuleProductAsync
import store.service.product.rest.{ProductRestService, ProductStockRestService}

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ModuleProductServiceAsync[F[_]] {
  this: ModuleProductAsync[F] with ModuleContentAsync[F] =>

  def productModuleService: HttpService[F] = _service

  def productRestService: ProductRestService[F] = _productRestService

  def stockRestService: ProductStockRestService[F] = _stockRestService

  private lazy val _productRestService: ProductRestService[F] =
    new ProductRestService[F](
      productAlgebra = productAlgebra
    )

  private lazy val _stockRestService: ProductStockRestService[F] =
    new ProductStockRestService[F](
      stockAlgebra = stockAlgebra
    )

  private lazy val _service = {
    import cats.implicits._
    NonEmptyList
      .of(
        productRestService.service,
        stockRestService.service
      )
      .reduceK
  }

}
