package store.service.product.rest

import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import store.algebra.content.ContentID
import store.algebra.content.entity.Content
import store.algebra.product._
import store.effects._
import store.algebra.product.entity.StoreProductDefinition
import store.algebra.product.entity.component._
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
    with ProductServiceJSON {

  private object ProductNameMatcher
      extends OptionalQueryParamDecoderMatcher[String]("name")
  private object ProductCategoryMatcher
      extends OptionalMultiQueryParamDecoderMatcher[Int]("c")
  private object SexMatcher
      extends OptionalQueryParamDecoderMatcher[String]("s")
  private object PageOffsetMatcher
      extends OptionalQueryParamDecoderMatcher[PageOffset]("offset")
  private object PageLimitMatcher
      extends OptionalQueryParamDecoderMatcher[PageLimit]("limit")

  private val categoryService: HttpService[F] = HttpService[F] {
    case GET -> Root / "category" :? SexMatcher(s) =>
      for {
        categories <- s.map(Sex.fromString).sequence match {
          case Left(a)  => F.raiseError[List[Category]](a.asThrowable)
          case Right(s) => productAlgebra.getCategories(s)
        }
        resp <- Ok(categories)
      } yield resp

    case request @ POST -> Root / "category" =>
      for {
        category <- request.as[CategoryDefinition]
        categoryId <- productAlgebra.createCategory(category)
        resp <- Created(categoryId)
      } yield resp

    case DELETE -> Root / "category" / IntVar(categoryId) =>
      for {
        _ <- productAlgebra.removeCategory(CategoryID(categoryId))
        resp <- Ok()
      } yield resp
  }

  private val promotionService: HttpService[F] = HttpService[F] {
    case GET -> Root / "promotion" =>
      for {
        promotions <- productAlgebra.getPromotions
        resp <- Ok(promotions)
      } yield resp

    case request @ POST -> Root / "promotion" =>
      for {
        content <- request.as[Content]
        promotionId <- productAlgebra.createPromotion(content)
        resp <- Created(promotionId)
      } yield resp

    case DELETE -> Root / "promotion" / promotionId =>
      for {
        _ <- productAlgebra.removePromotion(ContentID(promotionId))
        resp <- Ok()
      } yield resp
  }

  private val productService: HttpService[F] = HttpService[F] {
    case GET -> Root / "product" / LongVar(productId) =>
      for {
        product <- productAlgebra.getProduct(ProductID(productId))
        resp <- Ok(product)
      } yield resp

    case GET -> Root / "product" / LongVar(productId) / "navigation" =>
      for {
        product <- productAlgebra.getProductNavigation(ProductID(productId))
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
        productService,
        promotionService
      )
      .reduceK
  }
}
