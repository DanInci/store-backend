package store.algebra.order.entity

import store.algebra.product._
import store.algebra.product.entity.component._
import store.core._
import store.core.entity._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
final case class OrderedProductDefinition(
    productId: ProductID,
    size: ProductSize,
    count: Count
) extends Serializable

final case class OrderedProduct(
    productId: ProductID,
    category: Category,
    name: String,
    size: ProductSize,
    count: Count,
    price: Price,
    discount: Discount
)
