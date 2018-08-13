package store.service.product

import store.algebra.content.ModuleContentAsync
import store.algebra.product.ModuleProductAsync
import store.service.product.rest.{ProductRestService, ProductStockRestService}

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ModuleProductServiceAsync[F[_]] { this: ModuleProductAsync[F] with ModuleContentAsync[F] =>

  def productRestService: ProductRestService[F] = _productRestService

  def stockRestService: ProductStockRestService[F] = _stockRestService

  private lazy val _productRestService: ProductRestService[F] = new ProductRestService[F](
    productAlgebra = productAlgebra
  )

  private lazy val _stockRestService: ProductStockRestService[F] = new ProductStockRestService[F](
    stockAlgebra = stockAlgebra
  )

}
