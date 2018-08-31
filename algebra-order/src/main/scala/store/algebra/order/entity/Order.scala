package store.algebra.order.entity

import store.algebra.order._
import store.algebra.order.db.OrderSql.OrderDB
import store.algebra.order.entity.component.ShippingMethod

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
final case class OrderDefinition(
    buyer: Buyer,
    orderedProducts: List[OrderedProductDefinition],
    shippingMethodId: ShippingMethodID,
    billingFirstName: BillingFirstName,
    billingLastName: BillingLastName,
    billingAddress: BillingAddress,
    billingCity: BillingCity,
    billingCounty: BillingCounty,
    billingCountry: BillingCountry,
    billingPostalCode: BillingPostalCode,
    billingPhoneNumber: Option[BillingPhoneNumber]
) extends Serializable

final case class Order(
    orderId: OrderID,
    buyer: Buyer,
    orderedProducts: List[OrderedProduct],
    placedAt: PlacedAt,
    shippingMethod: ShippingMethod,
    billingFirstName: BillingFirstName,
    billingLastName: BillingLastName,
    billingAddress: BillingAddress,
    billingCity: BillingCity,
    billingCounty: BillingCounty,
    billingCountry: BillingCountry,
    billingPostalCode: BillingPostalCode,
    billingPhoneNumber: Option[BillingPhoneNumber]
) extends Serializable

object Order {
  def fromOrderDB(odb: OrderDB, orderedProducts: List[OrderedProduct]): Order =
    Order(
      odb.id,
      odb.buyer,
      orderedProducts,
      odb.placedAt,
      odb.shippingMethod,
      odb.billingFirstName,
      odb.billingLastName,
      odb.billingAddress,
      odb.billingCity,
      odb.billingCounty,
      odb.billingCountry,
      odb.billingPostalCode,
      odb.billingPhoneNumber
    )
}
