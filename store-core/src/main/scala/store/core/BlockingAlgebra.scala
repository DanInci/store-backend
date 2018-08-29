package store.core

import io.chrisdavenport.linebacker.DualContext
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait BlockingAlgebra[F[_]] {

  protected def block[M](thunk: => F[M])(implicit F: Async[F], dualContent: DualContext[F]): F[M] = {
    dualContent.block(thunk)
  }

}
