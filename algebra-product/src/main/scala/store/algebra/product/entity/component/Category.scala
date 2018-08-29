package store.algebra.product.entity.component

import store.algebra.product.CategoryID

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
final case class Category(
    categoryId: CategoryID,
    name: String,
    sex: Option[Sex]
) extends Serializable
