package store.service.product.rest

import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import store.algebra.product._
import store.algebra.product.entity.component._
import store.effects._
import store.http._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 19/01/2019
  */
final class CategoryRestService[F[_]](
    categoryAlgebra: CategoryAlgebra[F]
)(
    implicit F: Async[F]
) extends Http4sDsl[F]
    with ProductServiceJSON {

  private object SexMatcher
      extends OptionalQueryParamDecoderMatcher[String]("s")

  private val categoryService: HttpService[F] = HttpService[F] {
    case GET -> Root / "category" :? SexMatcher(s) =>
      for {
        categories <- s.map(Sex.fromString).sequence match {
          case Left(a)  => F.raiseError[List[Category]](a.asThrowable)
          case Right(s) => categoryAlgebra.getCategories(s)
        }
        resp <- Ok(categories)
      } yield resp

    case request @ POST -> Root / "category" =>
      for {
        category <- request.as[CategoryDefinition]
        categoryId <- categoryAlgebra.createCategory(category)
        resp <- Created(categoryId)
      } yield resp

    case DELETE -> Root / "category" / IntVar(categoryId) =>
      for {
        _ <- categoryAlgebra.removeCategory(CategoryID(categoryId))
        resp <- Ok()
      } yield resp
  }

  val service: HttpService[F] = categoryService

}
