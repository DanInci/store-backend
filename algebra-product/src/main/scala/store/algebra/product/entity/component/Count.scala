package store.algebra.product.entity.component

import busymachines.core.InvalidInputFailure
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 30/08/2018
  */
object Count {

  def apply(count: Int): Result[Count] =
    if (count >= 0) {
      Result.pure[Count](new Count(count))
    } else {
      Result.fail(InvalidInputFailure("Count must be a positive number."))
    }
}

final case class Count private (count: Int) extends Serializable {

  def +(other: Count): Count = Count(count + other.count).unsafeGet()

  def -(other: Count): Result[Count] = Count(count - other.count)

  override def toString: String = count.toString

}
