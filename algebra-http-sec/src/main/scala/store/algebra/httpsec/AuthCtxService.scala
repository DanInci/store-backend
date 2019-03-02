package store.algebra.httpsec

import cats.data.{Kleisli, OptionT}
import org.http4s.{AuthedRequest, AuthedService, Response}
import store.effects.Sync

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
object AuthCtxService {

  def apply[F[_]](
      pf: PartialFunction[AuthedRequest[F, Unit], F[Response[F]]]
  )(implicit S: Sync[F]): AuthedService[Unit, F] = {
    Kleisli(
      req =>
        pf.andThen(OptionT.liftF(_))
          .applyOrElse(req, Function.const(OptionT.none)))
  }

}
