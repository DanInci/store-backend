package store.algebra.order.impl

import busymachines.core.{AnomalousFailure, InvalidInputFailure}
import doobie._
import doobie.implicits._
import cats.implicits._
import doobie.util.transactor.Transactor
import store.algebra.order.entity._
import store.algebra.order._
import store.algebra.order.entity.component.ShippingMethod
import store.core.BlockingAlgebra
import store.core.entity.PagingInfo
import store.db.DatabaseContext
import store.effects._
import tsec.jws.mac._
import tsec.jwt._
import tsec.mac.jca._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
private[order] final class AsyncAlgebraImpl[F[_]](implicit val F: Async[F],
                                                  val transactor: Transactor[F],
                                                  val dbCtx: DatabaseContext[F])
    extends OrderAlgebra[F]
    with BlockingAlgebra[F] {

  import store.algebra.order.db.OrderSql._
  import store.algebra.product.db.ProductSql

  override def getOrders(startDate: Option[StartDate],
                         endDate: Option[EndDate],
                         pagingInfo: PagingInfo): F[List[Order]] = transact {
    findOrders(
      findAllByPlacedBetween(startDate,
                             endDate,
                             pagingInfo.offset,
                             pagingInfo.limit))
  }

  override def getOrder(id: OrderID): F[Option[Order]] = transact {
    findOrder(findById(id))
  }

  override def getOrder(token: OrderToken): F[Option[Order]] = transact {
    findOrder(findByOrderToken(token))
  }

  override def placeOrder(definition: OrderDefinition): F[OrderToken] =
    for {
      token <- generateToken().map(OrderToken.apply)
      _ <- transact {
        for {
          _ <- findShippingMethodById(definition.shippingMethodId).flatMap(
            exists(
              _,
              InvalidInputFailure(
                s"Shipping method with id ${definition.shippingMethodId} does not exist")))
          _ <- if (definition.orderedProducts.isEmpty)
            AsyncConnectionIO.raiseError[Unit](
              InvalidInputFailure("Ordered products list cannot be empty"))
          else
            definition.orderedProducts
              .map(checkAndUpdateStockForOrderedProduct)
              .sequence
          orderId <- insertOrderDefinition(definition, token)
          _ <- definition.orderedProducts
            .map(insertOrderedProductDefinition(_, orderId))
            .sequence
        } yield ()
      }
    } yield token

  private def checkAndUpdateStockForOrderedProduct(
      orderedProduct: OrderedProductDefinition): ConnectionIO[Unit] =
    for {
      product <- ProductSql
        .findById(orderedProduct.productId)
        .flatMap(
          exists(
            _,
            InvalidInputFailure(
              s"Product with id ${orderedProduct.productId} does not exist")))
      stock <- ProductSql
        .findStockByProductIdAndSize(orderedProduct.productId,
                                     orderedProduct.size)
        .flatMap(exists(
          _,
          InvalidInputFailure(
            s"'${product.name}' of size ${orderedProduct.size} is out of stock")))
      _ <- stock.count - orderedProduct.count match {
        case Left(_) =>
          AsyncConnectionIO.raiseError[Unit](InvalidInputFailure(
            s"Not enough stock remaining for '${product.name}' of size ${orderedProduct.size}. Left: ${stock.count}. Requested: ${orderedProduct.count}"))
        case Right(result) =>
          ProductSql.updateStockByProductIDAndSize(result,
                                                   orderedProduct.productId,
                                                   orderedProduct.size)
      }
    } yield ()

  override def getShippingMethods: F[List[ShippingMethod]] = transact {
    findAllShippingMethods
  }

  private def generateToken(): F[String] =
    for {
      key <- HMACSHA256.generateKey[F]
      claims = JWTClaims.default()
      token <- JWTMac.buildToString[F, HMACSHA256](claims, key)
    } yield token

  private def transact[A](query: ConnectionIO[A]): F[A] = {
    block(query.transact(transactor))
  }

  private def exists[A](value: Option[A],
                        failure: AnomalousFailure): ConnectionIO[A] =
    value match {
      case Some(x) => AsyncConnectionIO.pure[A](x)
      case None    => AsyncConnectionIO.raiseError(failure)
    }

}
