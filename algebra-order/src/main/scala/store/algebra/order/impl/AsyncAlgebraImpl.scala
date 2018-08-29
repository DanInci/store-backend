package store.algebra.order.impl

import doobie.util.transactor.Transactor
import store.algebra.order.entity._
import store.algebra.order._
import store.core.BlockingAlgebra
import store.core.entity.PagingInfo
import store.db.DatabaseContext
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
private[order] final class AsyncAlgebraImpl[F[_]](implicit val F: Async[F],
                                                  val transactor: Transactor[F],
                                                  val dbCtx: DatabaseContext[F])
    extends OrderAlgebra[F]
    with BlockingAlgebra[F] {

  override def getOrders(startDate: Option[StartDate], endDate: Option[EndDate], pagingInfo: PagingInfo): F[List[Order]] = ???

  override def placeOrder(definition: OrderDefinition): F[OrderToken] = ???

  override def getOrder(token: OrderToken): F[Option[Order]] = ???

  override def getOrder(id: OrderID): F[Option[Order]] = ???

}
