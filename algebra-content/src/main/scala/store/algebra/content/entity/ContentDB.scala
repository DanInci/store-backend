package store.algebra.content.entity

import store.algebra.content.ContentID

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 27/08/2018
  */
final case class ContentDB(
    contentId: ContentID,
    name: String
)
