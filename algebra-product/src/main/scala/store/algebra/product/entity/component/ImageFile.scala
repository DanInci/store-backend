package store.algebra.product.entity.component

import store.algebra.content.BinaryContent

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
final case class ImageFile(
    name: String,
    content: BinaryContent
) extends Serializable
