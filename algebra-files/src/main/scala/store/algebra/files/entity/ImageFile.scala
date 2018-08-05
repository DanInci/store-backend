package store.algebra.files.entity

import store.algebra.files.BinaryContent

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
final case class ImageFile(
    name: String,
    content: BinaryContent
)
