package store.algebra.product.entity

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 10/11/2018
  */
final case class ProductNavigation(
    previous: Option[StoreProduct],
    current: StoreProduct,
    next: Option[StoreProduct]
) extends Serializable
