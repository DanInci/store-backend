package store.algebra.product.entity.component

import store.algebra.content._
import store.algebra.content.entity.{Content, ContentDefinition, ContentLink}
import store.core.Link

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
final case class ImageFile(contentID: ContentID,
                           name: String,
                           content: BinaryContent)
    extends Content {
  override def format: Format = ImageFile.format
}

final case class ImageFileLink(name: String, link: Link) extends ContentLink {
  override def format: Format = ImageFile.format
}

final case class ImageFileDefinition(name: String, content: BinaryContent)
    extends ContentDefinition {
  override def format: Format = ImageFile.format
}

object ImageFile {
  def format: Format = Format("jpg")
}
