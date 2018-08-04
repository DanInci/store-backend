package store.service.product

import store.effects._
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl
import store.algebra.product.ProductAlgebra

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
final class ProductRestService[F[_]: Async](
    productAlgebra: ProductAlgebra[F]
) extends Http4sDsl[F] with ProductServiceJSON {

  private val productService: HttpService[F] = HttpService[F] {
    case GET -> Root / "product"  => ???
  }

  val service: HttpService[F] = productService
}
