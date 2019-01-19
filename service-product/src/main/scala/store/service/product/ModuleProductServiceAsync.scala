package store.service.product

import cats.data.NonEmptyList
import org.http4s.HttpService
import store.algebra.content.ModuleContentAsync
import store.algebra.product.ModuleProductAsync
import store.service.product.rest._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ModuleProductServiceAsync[F[_]] {
  this: ModuleProductAsync[F] with ModuleContentAsync[F] =>

  def productModuleService: HttpService[F] = _service

  def productRestService: ProductRestService[F] = _productRestService

  def categoryRestService: CategoryRestService[F] = _categoryRestService

  def promotionRestService: PromotionRestService[F] = _promotionRestService

  def stockRestService: ProductStockRestService[F] = _stockRestService

  private lazy val _productRestService: ProductRestService[F] =
    new ProductRestService[F](
      productAlgebra = productAlgebra
    )

  private lazy val _categoryRestService: CategoryRestService[F] =
    new CategoryRestService[F](
      categoryAlgebra = categoryAlgebra
    )

  private lazy val _promotionRestService: PromotionRestService[F] =
    new PromotionRestService[F](
      promotionAlgebra = promotionAlgebra
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
        categoryRestService.service,
        promotionRestService.service,
        stockRestService.service,
      )
      .reduceK
  }

}
