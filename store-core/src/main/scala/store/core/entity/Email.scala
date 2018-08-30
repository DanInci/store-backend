package store.core.entity

import busymachines.core.InvalidInputFailure
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
object Email {

  def apply(emailStr: String): Result[Email] =
    if (emailStr.contains("@")) {
      Result.pure(new Email(emailStr))
    } else {
      Result.fail(InvalidInputFailure("Email not valid"))
    }
}
final case class Email private (emailStr: String) extends Serializable {
  override def toString: String = emailStr
}
