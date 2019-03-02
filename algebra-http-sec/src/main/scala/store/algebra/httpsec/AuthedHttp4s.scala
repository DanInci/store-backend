package store.algebra.httpsec

import cats.implicits._
import busymachines.core.{Anomaly, UnauthorizedFailure}
import busymachines.json.AnomalyJsonCodec
import cats.data.{Kleisli, OptionT}
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRequest, AuthedService, Challenge, Request, headers}
import org.http4s.server.AuthMiddleware
import org.http4s.util.CaseInsensitiveString
import store.algebra.auth.{AuthAlgebra, AuthenticationToken}
import store.effects._
import store.http.Http4sCirceInstances

import scala.util.control.NonFatal

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
object AuthedHttp4s extends Http4sCirceInstances with AnomalyJsonCodec {

  def userTokenAuthMiddleware[F[_]: Async](
      authAlgebra: AuthAlgebra[F]): AuthMiddleware[F, Unit] =
    AuthMiddleware(verifyToken[F](authAlgebra), onFailure)

  private val `X-Auth-Token` = CaseInsensitiveString("X-AUTH-TOKEN")
  private val challenges: NonEmptyList[Challenge] = NonEmptyList.of(
    Challenge(
      scheme = "Bearer",
      realm = "Anca Store"
    )
  )

  private val wwwHeader = headers.`WWW-Authenticate`(challenges)

  private def onFailure[F[_]: Async]: AuthedService[Anomaly, F] = Kleisli {
    req: AuthedRequest[F, Anomaly] =>
      val fdsl = Http4sDsl[F]
      import fdsl._
      OptionT.liftF(Unauthorized(wwwHeader, req.authInfo.asInstanceOf[Anomaly]))
  }

  private def verifyToken[F[_]: Async](authAlgebra: AuthAlgebra[F])
    : Kleisli[F, Request[F], Result[Unit]] =
    Kleisli { req: Request[F] =>
      val F = Async.apply[F]

      val optHeader = req.headers.get(`X-Auth-Token`)
      optHeader match {
        case None =>
          F.pure(
            Result.fail(UnauthorizedFailure(s"No ${`X-Auth-Token`} provided")))
        case Some(header) =>
          authAlgebra
            .authenticate(AuthenticationToken(header.value))
            .map(Result.pure)
            .recover {
              case NonFatal(a: Anomaly) =>
                Result.fail(a)
              case NonFatal(a) =>
                Result.failThr(a)
            }
      }
    }

}
