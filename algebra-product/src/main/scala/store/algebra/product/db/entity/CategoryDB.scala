package store.algebra.product.db.entity

import store.algebra.product.CategoryID
import store.algebra.product.entity.component.Sex

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 29/08/2018
  */
final case class CategoryDB(
    categoryId: CategoryID,
    name: String,
    sex: Sex
)
