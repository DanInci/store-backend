package store.service.user.rest

import store.algebra.auth.AuthenticationToken
import store.json._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
trait UserServiceJSON extends StoreCoreJSON {

  implicit val productIDCirceCodec: Codec[AuthenticationToken] = phantomCodec[String, AuthenticationToken.Phantom]


}
