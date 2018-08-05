package store.service.product.rest

import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import store.algebra.files.FilesAlgebra
import store.algebra.product.ProductAlgebra
import store.core.ProductID
import store.effects._
import store.json._
import store.algebra.product.entity.StoreProduct

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
final class ProductRestService[F[_]: Async](
    productAlgebra: ProductAlgebra[F],
    filesAlgebra: FilesAlgebra[F]
) extends Http4sDsl[F] with ProductServiceJSON {

  private val productService: HttpService[F] = HttpService[F] {
    case GET -> Root / "product" / LongVar(productId) / "images"  =>
     for {
       images <- filesAlgebra.retrieveImagesForProduct(ProductID(productId))
       resp <- Ok(images)
     } yield resp
    case req @ POST -> Root / "product" =>
      for {
        product <- req.as[StoreProduct]
        _ <- filesAlgebra.saveImagesForProduct(ProductID(product.productId), product.images)
        resp <- Created(product)
      } yield resp
  }

  private val helloService: HttpService[F] = HttpService[F] {
    case GET -> Root / "hello" => Ok()
  }

  val service: HttpService[F] = {
    NonEmptyList.of(
      productService,
      helloService
    ).reduceK
  }
}
