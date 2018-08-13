package store.algebra.product.entity

import store.algebra.product.Count
import store.algebra.product.entity.component.ProductSize

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
final case class Stock(
    size: ProductSize,
    count: Count
) extends Serializable
