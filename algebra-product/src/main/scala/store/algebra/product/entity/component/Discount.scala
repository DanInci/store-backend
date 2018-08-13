package store.algebra.product.entity.component

import busymachines.core._
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
object Discount {

  def apply(percentage: Double): Result[Discount] =
    if (percentage >= 0.0 && percentage <= 100.0) {
      Result.pure[Discount](new Discount(percentage))
    } else {
      Result.fail(InvalidInputFailure("Not a valid percentage for discount."))
    }
}

final case class Discount private (percentage: Double)
    extends Serializable
