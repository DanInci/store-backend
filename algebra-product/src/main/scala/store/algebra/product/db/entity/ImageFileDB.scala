package store.algebra.product.db.entity

import store.algebra.content.ContentID

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
final case class ImageFileDB(
    name: String,
    contentId: ContentID
)
