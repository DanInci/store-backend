package store.algebra.content.entity

import store.effects._
import busymachines.core.InvalidInputFailure
import busymachines.effects.sync.Result

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 11/09/2018
  */
sealed trait Format extends Product with Serializable {
  def formatStr: String
  override def toString: String = formatStr
}

object Format {

  def fromString(formatStr: String): Result[Format] = {
    stringToFormat
      .get(formatStr)
      .asResult(InvalidInputFailure(s"Format $formatStr not accepted. Accepted formats: \n $allFormatsString"))
  }

  case object PNG extends Format {
    override def formatStr: String = "png"
  }

  case object JPG extends Format {
    override def formatStr: String = "jpg"
  }

  private lazy val stringToFormat: Map[String, Format] =
    allFormats.map(f => (f.toString, f)).toMap

  private lazy val allFormats: Set[Format] = Set(
    PNG,
    JPG
  )

  private lazy val allFormatsString = allFormats.mkString("[", ",", "]")

}