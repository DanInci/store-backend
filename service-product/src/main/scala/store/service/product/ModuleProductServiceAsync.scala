package store.service.product

import store.algebra.files.ModuleFilesAsync
import store.algebra.product.ModuleProductAsync
import store.service.product.rest.ProductRestService

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ModuleProductServiceAsync[F[_]] { this: ModuleProductAsync[F] with ModuleFilesAsync[F] =>

  def productRestService: ProductRestService[F] = _productRestService

  private lazy val _productRestService: ProductRestService[F] = new ProductRestService[F](
    productAlgebra = productAlgebra,
    filesAlgebra = filesAlgebra
  )

}
