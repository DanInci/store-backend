package store.algebra.order

import doobie.util.transactor.Transactor
import store.algebra.order.entity._
import store.core.entity.PagingInfo
import store.db.DatabaseContext
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
trait OrderAlgebra[F[_]] {

  def getOrders(startDate: Option[StartDate], endDate: Option[EndDate], pagingInfo: PagingInfo): F[List[Order]]

  def placeOrder(definition: OrderDefinition): F[OrderToken]

  def getOrder(token: OrderToken): F[Option[Order]]

  def getOrder(id: OrderID): F[Option[Order]]

}

object OrderAlgebra {

  def async[F[_]: Async](implicit transactor: Transactor[F],
                         dbCtx: DatabaseContext[F]): OrderAlgebra[F] =
    new impl.AsyncAlgebraImpl[F]()
}
