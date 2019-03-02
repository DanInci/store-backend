package store.algebra

import store.core.PhantomType

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 02/03/2019
  */
package object auth {

  object AuthenticationToken extends PhantomType[String]
  type AuthenticationToken = AuthenticationToken.Type

}
