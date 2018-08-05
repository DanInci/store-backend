package store.db

import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait DatabaseAlgebra[F[_]] {

  protected def shift[M](thunk: => F[M])(implicit F: Async[F], dbContext: DatabaseContext[F]): F[M] = {
    dbContext.block(thunk)
  }

}
