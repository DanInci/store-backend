package store.algebra.order.entity.component

import store.algebra.order.ShippingMethodID

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
final case class ShippingMethod(
    shippingMethodId: ShippingMethodID,
    name: String
) extends Serializable
