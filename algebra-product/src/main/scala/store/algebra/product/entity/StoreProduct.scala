package store.algebra.product.entity

import store.algebra.product._
import store.algebra.product.entity.component._
import store.core._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 06/08/2018
  */
final case class StoreProduct(
    productId: ProductID,
    categoryId: CategoryID,
    name: String,
    images: List[ImageFile],
    stocks: List[Stock],
    discount: Discount,
    isAvailableOnCommand: Boolean,
    description: List[DescParagraph],
    care: List[CareParagraph]
) extends Serializable
