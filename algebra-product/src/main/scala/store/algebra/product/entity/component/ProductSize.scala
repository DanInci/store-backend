package store.algebra.product.entity.component

import busymachines.core.InvalidInputFailure
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
sealed trait ProductSize
    extends Product
    with Serializable
    with Ordered[ProductSize]

object ProductSize {

  def fromString(s: String): Result[ProductSize] =
    nameToPerspective
      .get(s)
      .asResult(
        InvalidInputFailure(
          s"Product size has to be one of $allString, but was: $s"))

  implicit val productSizeOrdering: Ordering[ProductSize] =
    (x: ProductSize, y: ProductSize) => x.compare(y)

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

  private val LT = -1
  private val EQ = 0
  private val GT = 1

  case object XS extends ProductSize {
    override def compare(that: ProductSize): Int = that match {
      case XS => EQ
      case _  => LT
    }
  }
  case object S extends ProductSize {
    override def compare(that: ProductSize): Int = that match {
      case XS => GT
      case S  => EQ
      case _  => LT
    }
  }
  case object M extends ProductSize {
    override def compare(that: ProductSize): Int = that match {
      case XS => GT
      case S  => GT
      case M  => EQ
      case _  => LT
    }
  }
  case object L extends ProductSize {
    override def compare(that: ProductSize): Int = that match {
      case XL => LT
      case L  => EQ
      case _  => GT
    }
  }
  case object XL extends ProductSize {
    override def compare(that: ProductSize): Int = that match {
      case XL => EQ
      case _  => GT
    }
  }
}
