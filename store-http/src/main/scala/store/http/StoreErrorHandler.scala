package store.http

import cats.implicits._
import busymachines.core.{Anomaly, InvalidInputFailure, NotFoundFailure}
import busymachines.json.AnomalyJsonCodec
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import org.http4s._
import org.http4s.headers._
import org.http4s.server.ServiceErrorHandler
import org.http4s.util.CaseInsensitiveString
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 14/11/2018
  */
object StoreErrorHandler extends Http4sCirceInstances with AnomalyJsonCodec {

  def apply[F[_]](implicit F: Monad[F], logger: SelfAwareStructuredLogger[F]): ServiceErrorHandler[F] = req => {
    case e: InvalidInputFailure =>
      for {
        _ <- logger.info(s"InvalidInputFailure: ${req.method} ${req.pathInfo} from ${req.remoteAddr
          .getOrElse("<unknown>")}. Message: ${e.message}")
        resp <- Response[F](status = Status.BadRequest, httpVersion = req.httpVersion).withBody(e.asInstanceOf[Anomaly])
      } yield resp
    case e: NotFoundFailure =>
      for {
        _ <- logger.info(
          s"NotFoundFailure: ${req.method} ${req.pathInfo} from ${req.remoteAddr.getOrElse("<unknown>")}. Message: ${e.message}"
        )
        resp <- Response[F](status = Status.NotFound, httpVersion = req.httpVersion).withBody(e.asInstanceOf[Anomaly])
      } yield resp
    case mf: MessageFailure =>
      for {
        _ <- logger.warn(
          s"MessageFailure: ${req.method} ${req.pathInfo} from ${req.remoteAddr.getOrElse("<unknown>")}. ${mf.message}"
        )
        resp <- mf.toHttpResponse(req.httpVersion)(F)
      } yield resp
    case t if !t.isInstanceOf[VirtualMachineError] =>
      for {
        _ <- logger.error(t)(
          message =
            s"Servicing request: ${req.method} ${req.pathInfo} from ${req.remoteAddr.getOrElse("<unknown>")}"
        )
        resp = Response[F](
          status      = Status.InternalServerError,
          httpVersion = req.httpVersion,
          headers     = Headers(Connection(CaseInsensitiveString("close")), `Content-Length`.zero)
        )
      } yield resp
  }

}
