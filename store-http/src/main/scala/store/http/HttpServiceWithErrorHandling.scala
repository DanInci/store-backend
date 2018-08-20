package store.http

import cats.ApplicativeError
import cats.implicits._
import cats.data.{Kleisli, OptionT}
import org.http4s._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
object HttpServiceWithErrorHandling {

  def apply[F[_]](pf: PartialFunction[Request[F], F[Response[F]]])(
      implicit F: ApplicativeError[F, Throwable],
      handleError: Request[F] => Throwable => F[Response[F]])
    : HttpService[F] = {
    Kleisli(
      req =>
        pf.andThen(resp => resp.handleErrorWith(handleError(req)))
          .andThen(OptionT.liftF(_))
          .applyOrElse(req, Function.const(OptionT.none)))
  }

}
