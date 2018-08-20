package store.http

import busymachines.core._
import busymachines.json.AnomalyJsonCodec
import org.http4s.{MessageFailure, Request, Response}
import org.http4s.dsl.Http4sDsl
import store.effects.Monad

import scala.util.control.NonFatal

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
trait ErrorHandlingInstances[F[_]] {
  this: Http4sDsl[F] with AnomalyJsonCodec =>

  implicit protected def errorHandling(req: Request[F])(e: Throwable)(
      implicit M: Monad[F]): F[Response[F]] = e match {
    case NonFatal(e: InvalidInputFailure) => BadRequest(e.asInstanceOf[Anomaly])
    case NonFatal(e: ForbiddenFailure)    => Forbidden(e.asInstanceOf[Anomaly])
    case NonFatal(e: NotFoundFailure)     => NotFound(e.asInstanceOf[Anomaly])
    case NonFatal(e: MessageFailure)      => e.toHttpResponse(req.httpVersion)
  }

}
