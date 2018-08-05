package store.algebra.product.entity

import store.algebra.files.entity.ImageFile
import store.core.ProductID

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 06/08/2018
  */
final case class StoreProduct(
    productId: ProductID,
    images: List[ImageFile]
)
