package store.http

import busymachines.core.{Anomaly, ForbiddenFailure, InvalidInputFailure, NotFoundFailure}
import busymachines.json.AnomalyJsonCodec
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import store.effects.Monad

import scala.util.control.NonFatal

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
trait ErrorHandlingInstances[F[_]] extends AnomalyJsonCodec { this: Http4sDsl[F] =>

  implicit protected def errorHandling(e: Throwable)(implicit M: Monad[F]): F[Response[F]] = e match {
    case NonFatal(e: InvalidInputFailure) => BadRequest(e.asInstanceOf[Anomaly])
    case NonFatal(e: ForbiddenFailure)    => Forbidden(e.asInstanceOf[Anomaly])
    case NonFatal(e: NotFoundFailure)     => NotFound(e.asInstanceOf[Anomaly])
    case _ => InternalServerError()
  }

}
