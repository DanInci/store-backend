package store.service.product.rest

import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import store.algebra.product._
import store.effects._
import store.algebra.product.entity._
import store.http._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
final class ProductStockRestService[F[_]: Async](
    stockAlgebra: ProductStockAlgebra[F]
) extends Http4sDsl[F]
    with ErrorHandlingInstances[F]
    with ProductServiceJSON {

  private val stockService: HttpService[F] = HttpServiceWithErrorHandling {
    case GET -> Root / "product" / LongVar(productId) / "stock" =>
      for {
        stocks <- stockAlgebra.getStock(ProductID(productId))
        resp <- Ok(stocks)
      } yield resp

    case request @ POST -> Root / "product" / LongVar(productId) / "stock" / "add" =>
      for {
        stock <- request.as[Stock]
        _ <- stockAlgebra.addStock(stock, ProductID(productId))
        resp <- Ok()
      } yield resp

    case request @ POST -> Root / "product" / LongVar(productId) / "stock" / "remove" =>
      for {
        stock <- request.as[Stock]
        _ <- stockAlgebra.removeStock(stock, ProductID(productId))
        resp <- Ok()
      } yield resp
  }

  val service: HttpService[F] = stockService
}
