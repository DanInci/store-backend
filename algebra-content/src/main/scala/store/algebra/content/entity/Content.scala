package store.algebra.content.entity

import store.algebra.content._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 27/08/2018
  */
final case class Content(
    name: String,
    content: Either[ContentID, BinaryContent],
    format: Format
) extends Serializable
