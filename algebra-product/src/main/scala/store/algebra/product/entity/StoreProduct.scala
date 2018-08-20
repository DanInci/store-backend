package store.algebra.product.entity

import store.algebra.product._
import store.algebra.product.db.entity.StoreProductDB
import store.algebra.product.entity.component._
import store.core._
import store.core.entity._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 06/08/2018
  */
final case class StoreProduct(
    productId: ProductID,
    category: Category,
    name: String,
    images: List[ImageFile],
    stocks: List[Stock],
    price: Price,
    discount: Discount,
    isAvailableOnCommand: Boolean,
    description: List[DescParagraph],
    care: List[CareParagraph]
) extends Serializable

object StoreProduct {

  def fromStoreProductDB(
      spdb: StoreProductDB,
      stocks: List[Stock]
  ): StoreProduct =
    new StoreProduct(spdb.productId,
                     spdb.category,
                     spdb.name,
                     Nil,
                     stocks,
                     spdb.price,
                     spdb.discount,
                     spdb.isAvailableOnCommand,
                     spdb.description,
                     spdb.care)
}
