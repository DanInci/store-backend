package store.algebra.product.entity.component

import busymachines.core.InvalidInputFailure
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 29/08/2018
  */
sealed trait Sex extends Product with Serializable

object Sex {

  case object M extends Sex
  case object F extends Sex

  def fromString(s: String): Result[Sex] =
    nameToSex
      .get(s)
      .asResult(
        InvalidInputFailure(
          s"Product size has to be one of $allString, but was: $s"))

  private lazy val all: Set[Sex] = Set(
    M,
    F
  )

  private lazy val nameToSex: Map[String, Sex] =
    all.map(s => (s.productPrefix, s)).toMap

  private lazy val allString = all.mkString("[", ",", "]")

}
