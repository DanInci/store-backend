package store.service.order.rest

import java.time.LocalDate

import busymachines.core.NotFoundFailure
import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import store.algebra.email._
import store.algebra.order._
import store.algebra.order.entity._
import store.core.entity.PagingInfo
import store.core.{PageLimit, PageOffset}
import store.effects._
import store.http._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
final class OrderRestService[F[_]](
    orderAlgebra: OrderAlgebra[F],
    emailAlgebra: EmailAlgebra[F]
)(
    implicit F: Async[F]
) extends Http4sDsl[F]
    with ErrorHandlingInstances[F]
    with OrderServiceJSON {

  private object OrderCodeMatcher
      extends QueryParamDecoderMatcher[String]("code")
  private object StartDateMatcher
      extends OptionalQueryParamDecoderMatcher[LocalDate]("startDate")
  private object EndDateMatcher
      extends OptionalQueryParamDecoderMatcher[LocalDate]("endDate")
  private object PageOffsetMatcher
      extends OptionalQueryParamDecoderMatcher[PageOffset]("offset")
  private object PageLimitMatcher
      extends OptionalQueryParamDecoderMatcher[PageLimit]("limit")

  private val orderPlacementService: HttpService[F] =
    HttpServiceWithErrorHandling {
      case request @ POST -> Root / "order" =>
        for {
          orderDef <- request.as[OrderDefinition]
          token <- orderAlgebra.placeOrder(orderDef)
          subject = EmailSubject(
            s"Congrats ${orderDef.buyer.firstName}. Your order has been placed !")
          content = EmailContent(
            s"Your order has been placed.<br>You can see order details with this code: $token")
          _ <- emailAlgebra.sendEmail(orderDef.buyer.email, subject, content)
          resp <- Created(token)
        } yield resp
    }

  private val orderService: HttpService[F] = HttpServiceWithErrorHandling {
    case GET -> Root / "order" :? StartDateMatcher(startDate) +& EndDateMatcher(
          endDate) +& PageOffsetMatcher(offset) +& PageLimitMatcher(limit) =>
      for {
        orders <- orderAlgebra.getOrders(
          startDate.map(StartDate.apply),
          endDate.map(EndDate.apply),
          PagingInfo(offset, limit)
        )
        resp <- Ok(orders)
      } yield resp

    case GET -> Root / "order" / LongVar(id) =>
      for {
        order <- orderAlgebra.getOrder(OrderID(id)).flatMap {
          case Some(o) => F.pure(o)
          case None =>
            F.raiseError[Order](NotFoundFailure(s"Order with id $id not found"))
        }
        resp <- Ok(order)
      } yield resp

    case GET -> Root / "order" / "placed" :? OrderCodeMatcher(code) =>
      for {
        order <- orderAlgebra.getOrder(OrderToken(code)).flatMap {
          case Some(o) => F.pure(o)
          case None =>
            F.raiseError[Order](NotFoundFailure(s"Order was not found"))
        }
        resp <- Ok(order)
      } yield resp
  }

  private val shippingMethodService: HttpService[F] =
    HttpServiceWithErrorHandling {
      case GET -> Root / "shipping" =>
        for {
          shippingMethods <- orderAlgebra.getShippingMethods
          resp <- Ok(shippingMethods)
        } yield resp
    }

  val service: HttpService[F] = {
    NonEmptyList
      .of(
        orderPlacementService,
        orderService,
        shippingMethodService
      )
      .reduceK
  }

}
