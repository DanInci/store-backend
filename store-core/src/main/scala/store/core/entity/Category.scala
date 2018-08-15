package store.core.entity

import store.core.CategoryID

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
final case class Category(
    categoryId: CategoryID,
    name: String
) extends Serializable
