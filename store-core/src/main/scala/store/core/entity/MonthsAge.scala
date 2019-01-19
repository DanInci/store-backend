package store.core.entity

import busymachines.core.InvalidInputFailure
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 19/01/2019
  */
object MonthsAge {

  def apply(age: Int): Result[MonthsAge] = {
    if (age > 0) {
      Result.pure[MonthsAge](new MonthsAge(age))
    } else {
      Result.fail(InvalidInputFailure("Months age must be above 0"))
    }
  }
}

final case class MonthsAge private (age: Int) extends Serializable
