package store.algebra.order.db

import java.time.LocalDateTime

import doobie._
import doobie.implicits._
import cats.implicits._
import store.algebra.order._
import store.algebra.order.entity._
import store.algebra.order.entity.component._
import store.core._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
object OrderSql extends OrderComposites {

  private[order] case class OrderDB(
      id: OrderID,
      buyer: Buyer,
      placedAt: PlacedAt,
      shippingMethod: ShippingMethod,
      billingFirstName: BillingFirstName,
      billingLastName: BillingLastName,
      billingAddress: BillingAddress,
      billingCity: BillingCity,
      billingCounty: BillingCounty,
      billingCountry: BillingCountry,
      billingPostalCode: BillingPostalCode,
      billingPhoneNumber: BillingPhoneNumber
  )

  def findAllByPlacedBetween(startDate: Option[StartDate],
                             endDate: Option[EndDate],
                             offset: PageOffset,
                             limit: PageLimit): ConnectionIO[List[OrderDB]] = {
    val whereClause = if (startDate.isDefined && endDate.isDefined) {
      s"WHERE placed_at BETWEEN ${startDate.get} AND ${endDate.get}"
    } else if (startDate.isDefined) {
      s"WHERE placed_at >= ${startDate.get}"
    } else if (endDate.isDefined) {
      s"WHERE placed_at <= ${endDate.get}"
    } else ""

    (sql"""SELECT o_order_id, b.email, b.subscribed, b.firstname, b.lastname, b.address, b.city, b.county, b.country, b.postal_code, b.phone_number, placed_at, sm.shipping_method_id, sm.name, billing_firstname, billing_lastname, billing_address, billing_city, billing_county, billing_country, billing_postal_code, billing_phone_number
         | FROM "order" o
         | JOIN buyer b on o.order_id = b.o_order_id
         | JOIN shipping_method sm on o.sm_shipping_method_id = sm.shipping_method_id""".stripMargin
      ++ Fragment.const(whereClause) ++
      sql" LIMIT $limit OFFSET ${offset * limit}")
      .query[OrderDB]
      .to[List]
  }

  def findById(id: OrderID): ConnectionIO[Option[OrderDB]] =
    sql"""SELECT o_order_id, b.email, b.subscribed, b.firstname, b.lastname, b.address, b.city, b.county, b.country, b.postal_code, b.phone_number, placed_at, sm.shipping_method_id, sm.name, billing_firstname, billing_lastname, billing_address, billing_city, billing_county, billing_country, billing_postal_code, billing_phone_number
         | FROM "order" o
         | JOIN buyer b on o.order_id = b.o_order_id
         | JOIN shipping_method sm on o.sm_shipping_method_id = sm.shipping_method_id
         | WHERE o_order_id = $id""".stripMargin.query[OrderDB].option

  def findByOrderToken(token: OrderToken): ConnectionIO[Option[OrderDB]] =
    sql"""SELECT o_order_id, b.email, b.subscribed, b.firstname, b.lastname, b.address, b.city, b.county, b.country, b.postal_code, b.phone_number, placed_at, sm.shipping_method_id, sm.name, billing_firstname, billing_lastname, billing_address, billing_city, billing_county, billing_country, billing_postal_code, billing_phone_number
         | FROM "order" o
         | JOIN buyer b on o.order_id = b.o_order_id
         | JOIN shipping_method sm on o.sm_shipping_method_id = sm.shipping_method_id
         | WHERE order_token = $token""".stripMargin.query[OrderDB].option

  def findBuyerByOrderId(id: OrderID): ConnectionIO[Option[Buyer]] =
    sql"""SELECT email, subscribed, firstname, lastname, address, city, county, country, postal_code, phone_number
         | FROM buyer
         | WHERE o_order_id=$id""".stripMargin.query[Buyer].option

  def findOrderedProductsByOrderId(
      id: OrderID): ConnectionIO[List[OrderedProduct]] =
    sql"""SELECT p_product_id, c.category_id, c.name, c.sex, p.name, product_size, ordered_count, (ordered_count * p.price) AS price, p.discount
         | FROM ordered_product op
         | JOIN product p on op.p_product_id = p.product_id
         | JOIN category c on p.c_category_id = c.category_id
         | WHERE o_order_id = $id""".stripMargin.query[OrderedProduct].to[List]

  def insertOrderDefinition(definition: OrderDefinition,
                            token: OrderToken): ConnectionIO[OrderID] =
    AsyncConnectionIO
      .pure(LocalDateTime.now())
      .flatMap(now =>
        sql"""INSERT INTO "order" 
         | (sm_shipping_method_id, placed_at, billing_firstname, billing_lastname, billing_address, billing_city, billing_country, billing_county, billing_postal_code, billing_phone_number, order_token) 
         | VALUES (${definition.shippingMethodId}, $now, ${definition.billingFirstName}, ${definition.billingLastName},  ${definition.billingAddress}, ${definition.billingCity}, ${definition.billingCounty}, ${definition.billingCountry},${definition.billingPostalCode}, ${definition.billingPhoneNumber},  $token)""".stripMargin.update
          .withUniqueGeneratedKeys[OrderID]("order_id"))

  def insertBuyer(buyer: Buyer, orderId: OrderID): ConnectionIO[BuyerID] =
    sql"""INSERT INTO buyer
         | (o_order_id, email, subscribed, firstname, lastname, address, city, county, country, postal_code, phone_number)
         | VALUES ($orderId, ${buyer.email}, ${buyer.isSubscribed},${buyer.firstName}, ${buyer.lastName}, ${buyer.address}, ${buyer.city}, ${buyer.county}, ${buyer.country}, ${buyer.postalCode}, ${buyer.phoneNumber})""".stripMargin.update
      .withUniqueGeneratedKeys[BuyerID]("buyer_id")

  def findBuyerById(id: BuyerID): ConnectionIO[Option[Buyer]] =
    sql"""SELECT email, subscribed, firstname, lastname, address, city, county, country, postal_code, phone_number
         | FROM buyer
         | WHERE buyer_id=$id""".stripMargin.query[Buyer].option

  def insertOrderedProductDefinition(
      definition: OrderedProductDefinition,
      orderId: OrderID): ConnectionIO[OrderedProductID] =
    sql"""INSERT INTO ordered_product
         | (p_product_id, o_order_id, product_size, ordered_count)
         | VALUES (${definition.productId}, $orderId, ${definition.size}, ${definition.count})""".stripMargin.update
      .withUniqueGeneratedKeys[OrderedProductID]("ordered_product_id")

  def findOrderedProductById(
      id: OrderedProductID): ConnectionIO[Option[OrderedProduct]] =
    sql"""SELECT p_product_id, c.category_id, c.name, c.sex, p.name, product_size, ordered_count, (ordered_count * p.price) AS price, p.discount
         | FROM ordered_product op
         | JOIN product p on op.p_product_id = p.product_id
         | JOIN category c on p.c_category_id = c.category_id
         | WHERE ordered_product_id = $id""".stripMargin
      .query[OrderedProduct]
      .option

  def findShippingMethodById(id: ShippingMethodID): ConnectionIO[Option[ShippingMethod]] =
    sql"""SELECT shipping_method_id, name
         | FROM shipping_method
         | WHERE shipping_method_id=$id""".stripMargin.query[ShippingMethod].option

  def findAllShippingMethods: ConnectionIO[List[ShippingMethod]] =
    sql"SELECT shipping_method_id, name FROM shipping_method".query[ShippingMethod].to[List]

  def findOrder(
      req: => ConnectionIO[Option[OrderDB]]): ConnectionIO[Option[Order]] =
    req.flatMap {
      case Some(o) =>
        findOrderedProductsByOrderId(o.id).map(p =>
          Some(Order.fromOrderDB(o, p)))
      case None => AsyncConnectionIO.pure(Option.empty[Order])
    }

  def findOrders(
      req: => ConnectionIO[List[OrderDB]]): ConnectionIO[List[Order]] =
    req.flatMap(
      _.map(o =>
        findOrderedProductsByOrderId(o.id)
          .map(p => Order.fromOrderDB(o, p))).sequence)

}
