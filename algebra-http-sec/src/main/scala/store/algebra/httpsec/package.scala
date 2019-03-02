package store.algebra

import org.http4s.AuthedService
import org.http4s.server.AuthMiddleware

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
package object httpsec {

  type AuthCtxService[F[_]] = AuthedService[Unit, F]
  type AuthCtxMiddleware[F[_]] = AuthMiddleware[F, Unit]

}
