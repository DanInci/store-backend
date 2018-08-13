package store.algebra.product.entity.component

import busymachines.core.InvalidInputFailure
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
sealed trait ProductSize extends Product with Serializable

object ProductSize {

  case object XS extends ProductSize
  case object S extends ProductSize
  case object M extends ProductSize
  case object L extends ProductSize
  case object XL extends ProductSize

  def fromString(s: String): Result[ProductSize] =
    nameToPerspective
      .get(s)
      .asResult(InvalidInputFailure(s"Product size has to be one of $allString, but was: $s"))

  private lazy val all: Set[ProductSize] = Set(
    XS,
    S,
    M,
    L,
    XL
  )

  private lazy val nameToPerspective: Map[String, ProductSize] =
    all.map(s => (s.productPrefix, s)).toMap

  private lazy val allString = all.mkString("[", ",", "]")
}
