package store.algebra

import java.time.{LocalDate, LocalDateTime}

import store.core.PhantomType

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
package object order {

  object OrderID extends PhantomType[Long]
  type OrderID = OrderID.Type

  object OrderedProductID extends PhantomType[Long]
  type OrderedProductID = OrderedProductID.Type

  object BuyerID extends PhantomType[Long]
  type BuyerID = BuyerID.Type

  object ShippingMethodID extends PhantomType[Int]
  type ShippingMethodID = ShippingMethodID.Type

  object PlacedAt extends PhantomType[LocalDateTime]
  type PlacedAt = PlacedAt.Type

  object StartDate extends PhantomType[LocalDate]
  type StartDate = StartDate.Type

  object EndDate extends PhantomType[LocalDate]
  type EndDate = EndDate.Type

  object FirstName extends PhantomType[String]
  type FirstName = FirstName.Type

  object BillingFirstName extends PhantomType[String]
  type BillingFirstName = BillingFirstName.Type

  object LastName extends PhantomType[String]
  type LastName = LastName.Type

  object BillingLastName extends PhantomType[String]
  type BillingLastName = BillingLastName.Type

  object Address extends PhantomType[String]
  type Address = Address.Type

  object BillingAddress extends PhantomType[String]
  type BillingAddress = BillingAddress.Type

  object City extends PhantomType[String]
  type City = City.Type

  object BillingCity extends PhantomType[String]
  type BillingCity = BillingCity.Type

  object County extends PhantomType[String]
  type County = County.Type

  object BillingCounty extends PhantomType[String]
  type BillingCounty = BillingCounty.Type

  object Country extends PhantomType[String]
  type Country = Country.Type

  object BillingCountry extends PhantomType[String]
  type BillingCountry = BillingCountry.Type

  object PostalCode extends PhantomType[String]
  type PostalCode = PostalCode.Type

  object BillingPostalCode extends PhantomType[String]
  type BillingPostalCode = BillingPostalCode.Type

  object PhoneNumber extends PhantomType[String]
  type PhoneNumber = PhoneNumber.Type

  object BillingPhoneNumber extends PhantomType[String]
  type BillingPhoneNumber = BillingPhoneNumber.Type

  object OrderToken extends PhantomType[String]
  type OrderToken = OrderToken.Type

}
