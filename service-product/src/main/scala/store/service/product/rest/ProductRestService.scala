package store.service.product.rest

import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import store.algebra.product._
import store.effects._
import store.algebra.product.entity.StoreProductDefinition
import store.core._
import store.core.entity._
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
  private object ProductMonthsAgeMatcher
      extends OptionalQueryParamDecoderMatcher[Int]("age")
  private object ProductFavouritesMatcher
      extends OptionalQueryParamDecoderMatcher[Boolean]("favourite")
  private object ProductCategoryMatcher
      extends OptionalMultiQueryParamDecoderMatcher[Int]("c")
  private object PageOffsetMatcher
      extends OptionalQueryParamDecoderMatcher[PageOffset]("offset")
  private object PageLimitMatcher
      extends OptionalQueryParamDecoderMatcher[PageLimit]("limit")

  private val productService: HttpService[F] = HttpService[F] {
    case GET -> Root / "product" / LongVar(productId) =>
      for {
        product <- productAlgebra.getProduct(ProductID(productId))
        resp <- Ok(product)
      } yield resp

    case GET -> Root / "product" / LongVar(productId) / "navigation" :? ProductNameMatcher(
          name) +& ProductCategoryMatcher(categories) +& ProductMonthsAgeMatcher(
          age) +& ProductFavouritesMatcher(isFavourite) =>
      val checkedName = name.filterNot(
        n =>
          n.contains("'") || n.contains("DELETE") || n.contains("SELECT") || n
            .contains("UPDATE"))
      for {
        monthsAge <- getMonthsAge(age)
        product <- productAlgebra.getProductNavigation(
          ProductID(productId),
          checkedName,
          categories.toOption.getOrElse(Nil).map(CategoryID.apply),
          monthsAge,
          isFavourite)
        resp <- Ok(product)
      } yield resp

    case GET -> Root / "product" :? ProductNameMatcher(name) +& ProductCategoryMatcher(
          categories) +& ProductMonthsAgeMatcher(age) +& ProductFavouritesMatcher(
          isFavourite) +& PageOffsetMatcher(offset) +& PageLimitMatcher(
          limit) =>
      val checkedName = name.filterNot(
        n =>
          n.contains("'") || n.contains("DELETE") || n.contains("SELECT") || n
            .contains("UPDATE"))
      for {
        monthsAge <- getMonthsAge(age)
        products <- productAlgebra.getProducts(
          checkedName,
          categories.toOption.getOrElse(Nil).map(CategoryID.apply),
          monthsAge,
          isFavourite,
          PagingInfo(offset, limit))
        resp <- Ok(products)
      } yield resp

    case GET -> Root / "product" / "count" :? ProductNameMatcher(name) +& ProductCategoryMatcher(
          categories) +& ProductMonthsAgeMatcher(age) +& ProductFavouritesMatcher(
          isFavourite) =>
      val checkedName = name.filterNot(
        n =>
          n.contains("'") || n.contains("DELETE") || n.contains("SELECT") || n
            .contains("UPDATE"))
      for {
        monthsAge <- getMonthsAge(age)
        count <- productAlgebra.getProductsCount(
          checkedName,
          categories.toOption.getOrElse(Nil).map(CategoryID.apply),
          monthsAge,
          isFavourite)
        resp <- Ok(count)
      } yield resp

    case request @ POST -> Root / "product" =>
      for {
        productDefinition <- request.as[StoreProductDefinition]
        productId <- productAlgebra.createProduct(productDefinition)
        resp <- Created(productId)
      } yield resp

    case request @ PUT -> Root / "product" / LongVar(productId) =>
      for {
        productDefinition <- request.as[StoreProductDefinition]
        productId <- productAlgebra.updateProduct(ProductID(productId), productDefinition)
        resp <- Ok(productId)
      } yield resp

    case DELETE -> Root / "product" / LongVar(productId) =>
      for {
        _ <- productAlgebra.removeProduct(ProductID(productId))
        resp <- Ok()
      } yield resp
  }

  private def getMonthsAge(age: Option[Int]): F[Option[MonthsAge]] = {
    if (age.isDefined) {
      val monthsAge = MonthsAge(age.get)
      if (monthsAge.isRight) {
        F.pure(Some(monthsAge.unsafeGet()))
      } else {
        F.raiseError(monthsAge.left.get asThrowable)
      }
    } else {
      F.pure[Option[MonthsAge]](None)
    }
  }

  val service: HttpService[F] = productService

}
