package store.algebra.product.entity

import store.algebra.content.entity.Content
import store.algebra.product.entity.component.Discount
import store.algebra.product._
import store.algebra.product.db.entity.StoreProductDB

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 09/11/2018
  */
case class ProductPromotion(
    productId: ProductID,
    name: String,
    price: Price,
    discount: Discount,
    image: Content
) extends Serializable

object ProductPromotion {
  def fromStoreProductDB(storeProduct: StoreProductDB,
                         promotionImage: Content): ProductPromotion =
    ProductPromotion(storeProduct.productId,
                     storeProduct.name,
                     storeProduct.price,
                     storeProduct.discount,
                     promotionImage)
}
