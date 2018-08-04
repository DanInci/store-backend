package store.service.product

import store.algebra.product.ModuleProductAsync

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ModuleProductServiceAsync[F[_]] { this: ModuleProductAsync[F] =>

  def productRestService: ProductRestService[F] = _productRestService

  private lazy val _productRestService: ProductRestService[F] = new ProductRestService[F](
    productAlgebra = productAlgebra
  )

}
