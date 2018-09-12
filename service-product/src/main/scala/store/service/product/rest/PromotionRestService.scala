package store.service.product.rest

import cats.implicits._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import store.algebra.product._
import store.algebra.product.entity.PromotionDefinition
import store.effects.Async
import store.http._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 11/09/2018
  */
final class PromotionRestService[F[_]](
    promotionAlgebra: PromotionAlgebra[F]
)(
    implicit F: Async[F]
) extends Http4sDsl[F]
    with ErrorHandlingInstances[F]
    with ProductServiceJSON {

  private val promotionService: HttpService[F] =
    HttpServiceWithErrorHandling[F] {
      case GET -> Root / "promotion" =>
        for {
          promotions <- promotionAlgebra.getAllPromotions
          resp <- Ok(promotions)
        } yield resp

      case GET -> Root / "promotion" / "active" =>
        for {
          activePromotions <- promotionAlgebra.getActivePromotions
          resp <- Ok(activePromotions)
        } yield resp

      case GET -> Root / "promotion" / LongVar(id) =>
        for {
          promotion <- promotionAlgebra.getPromotionById(PromotionID(id))
          resp <- Ok(promotion)
        } yield resp

      case req @ POST -> Root / "promotion" =>
        for {
          definition <- req.as[PromotionDefinition]
          promotionId <- promotionAlgebra.createPromotion(definition)
          resp <- Created(promotionId)
        } yield resp

      case DELETE -> Root / "promotion" / LongVar(id) =>
        for {
          _ <- promotionAlgebra.deletePromotion(PromotionID(id))
          resp <- Ok()
        } yield resp
    }

  val service: HttpService[F] = promotionService
}
