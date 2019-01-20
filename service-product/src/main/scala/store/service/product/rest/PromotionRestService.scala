package store.service.product.rest

import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import store.algebra.content.ContentID
import store.algebra.content.entity.Content
import store.algebra.product._
import store.effects._
import store.http._

import java.util.Base64

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 19/01/2019
  */
final class PromotionRestService[F[_]](
    promotionAlgebra: PromotionAlgebra[F]
)(
    implicit F: Async[F]
) extends Http4sDsl[F]
    with ProductServiceJSON {

  private val promotionService: HttpService[F] = HttpService[F] {
    case GET -> Root / "promotion" =>
      for {
        promotions <- promotionAlgebra.getPromotions
        resp <- Ok(promotions)
      } yield resp

    case request @ POST -> Root / "promotion" =>
      for {
        content <- request.as[Content]
        promotionId <- promotionAlgebra.createPromotion(content)
        resp <- Created(promotionId)
      } yield resp

    case DELETE -> Root / "content" / base64EncodedContetId =>
      val contentId = Base64.getDecoder.decode(base64EncodedContetId).map(_.toChar).mkString
      for {
        _ <- promotionAlgebra.removeContent(ContentID(contentId))
        resp <- Ok()
      } yield resp
  }

  val service: HttpService[F] = promotionService

}
