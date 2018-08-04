package store.algebra.product.impl

import store.effects._
import store.db.DatabaseContext
import doobie.Transactor
import store.algebra.product.ProductAlgebra

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
final private[product] class AsyncAlgebraImpl[F[_]](
    implicit
    val F: Async[F],
    val monadError: MonadError[F, Throwable],
    val transactor: Transactor[F],
    val dbContext: DatabaseContext[F]
) extends ProductAlgebra[F] {}
