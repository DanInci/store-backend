package store.service.product.rest

import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import store.algebra.product._
import store.effects._
import store.algebra.product.entity.StoreProductDefinition
import store.core._
import store.core.entity.PagingInfo
import store.http._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
final class ProductRestService[F[_]](
    productAlgebra: ProductAlgebra[F]
)(
    implicit F: Async[F]
) extends Http4sDsl[F]
    with ErrorHandlingInstances[F]
    with ProductServiceJSON {

  private object ProductNameMatcher
      extends OptionalQueryParamDecoderMatcher[String]("name")
  private object ProductCategoryMatcher
      extends OptionalMultiQueryParamDecoderMatcher[Int]("c")
  private object PageOffsetMatcher
      extends OptionalQueryParamDecoderMatcher[PageOffset]("offset")
  private object PageLimitMatcher
      extends OptionalQueryParamDecoderMatcher[PageLimit]("limit")

  private val categoryService: HttpService[F] =
    HttpServiceWithErrorHandling[F] {
      case GET -> Root / "category" =>
        for {
          categories <- productAlgebra.getCategories
          resp <- Ok(categories)
        } yield resp
    }

  private val productService: HttpService[F] = HttpServiceWithErrorHandling[F] {
    case GET -> Root / "product" / LongVar(productId) =>
      for {
        product <- productAlgebra.getProduct(ProductID(productId))
        resp <- Ok(product)
      } yield resp

    case GET -> Root / "product" :? ProductNameMatcher(name) +& ProductCategoryMatcher(
          categories) +& PageOffsetMatcher(offset) +& PageLimitMatcher(limit) =>
      for {
        products <- productAlgebra.getProducts(
          name,
          categories.toOption.getOrElse(Nil).map(CategoryID.apply),
          PagingInfo(offset, limit))
        resp <- Ok(products)
      } yield resp

    case request @ POST -> Root / "product" =>
      for {
        productDefinition <- request.as[StoreProductDefinition]
        productId <- productAlgebra.createProduct(productDefinition)
        resp <- Created(productId)
      } yield resp

    case DELETE -> Root / "product" / LongVar(productId) =>
      for {
        _ <- productAlgebra.removeProduct(ProductID(productId))
        resp <- Ok()
      } yield resp
  }

  val service: HttpService[F] = {
    NonEmptyList
      .of(
        categoryService,
        productService
      )
      .reduceK
  }
}
