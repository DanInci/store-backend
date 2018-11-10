package store.algebra

import store.core.PhantomType

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
package object email {

  object EmailSubject extends PhantomType[String]
  type EmailSubject = EmailSubject.Type

  object EmailContent extends PhantomType[String]
  type EmailContent = EmailContent.Type

}
