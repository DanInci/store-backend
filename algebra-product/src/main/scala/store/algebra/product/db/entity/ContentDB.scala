package store.algebra.product.db.entity

import store.algebra.content.ContentID
import store.algebra.content.entity.Format

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 09/11/2018
  */
final case class ContentDB(
    contentId: ContentID,
    name: String,
    format: Format,
    isPromotionImage: Boolean
)
