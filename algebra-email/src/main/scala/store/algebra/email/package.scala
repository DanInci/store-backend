package store.algebra

import store.core.PhantomType

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
package object email {

  object Subject extends PhantomType[String]
  type Subject = Subject.Type

  object Content extends PhantomType[String]
  type Content = Content.Type

}
