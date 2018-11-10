package store.service.order.rest

import java.time.LocalDateTime

import store.algebra.email.EmailContent
import store.algebra.order._
import store.algebra.order.entity._
import store.algebra.order.entity.component._
import store.algebra.product._
import store.algebra.product.entity.component._
import store.core.entity.Email
import store.json._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait OrderServiceJSON extends StoreCoreJSON {

  implicit val sexCirceCodec: Codec[Sex] = Codec.instance[Sex](
    encode = Encoder.apply[String].contramap(sex => sex.productPrefix),
    decode =
      Decoder.apply[String].emap(s => Sex.fromString(s).left.map(_.message))
  )

  implicit val productSizeCirceCodec: Codec[ProductSize] =
    Codec.instance[ProductSize](
      encode = Encoder.apply[String].contramap(_.productPrefix),
      decode = Decoder
        .apply[String]
        .emap(ProductSize.fromString(_).left.map(_.message))
    )

  implicit val countCirceCodec: Codec[Count] = Codec.instance[Count](
    encode = Encoder.apply[Int].contramap(_.count),
    decode = Decoder.apply[Int].emap(Count.apply(_).left.map(_.message))
  )

  implicit val emailCirceCodec: Codec[Email] = Codec.instance[Email](
    encode = Encoder.apply[String].contramap(email => email.emailStr),
    decode = Decoder.apply[String].emap(s => Email(s).left.map(_.message))
  )

  implicit val discountCirceCodec: Codec[Discount] = Codec.instance[Discount](
    encode = Encoder.apply[Double].contramap(_.percentage),
    decode = Decoder.apply[Double].emap(Discount(_).left.map(_.message))
  )

  implicit val productIDCirceCodec: Codec[ProductID] = phantomCodec[Long, ProductID.Phantom]

  implicit val categoryIDCirceCodec: Codec[CategoryID] = phantomCodec[Int, CategoryID.Phantom]

  implicit val orderIDCirceCodec: Codec[OrderID] = phantomCodec[Long, OrderID.Phantom]

  implicit val firstNameCirceCodec: Codec[FirstName] = phantomCodec[String, FirstName.Phantom]

  implicit val lastNameCirceCodec: Codec[LastName] = phantomCodec[String, LastName.Phantom]

  implicit val addressCirceCodec: Codec[Address] = phantomCodec[String, Address.Phantom]

  implicit val cityCirceCodec: Codec[City] = phantomCodec[String, City.Phantom]

  implicit val countyCirceCodec: Codec[County] = phantomCodec[String, County.Phantom]

  implicit val countryCirceCodec: Codec[Country] = phantomCodec[String, Country.Phantom]

  implicit val postalCodeCirceCodec: Codec[PostalCode] = phantomCodec[String, PostalCode.Phantom]

  implicit val phoneNumberCirceCodec: Codec[PhoneNumber] = phantomCodec[String, PhoneNumber.Phantom]

  implicit val priceCirceCodec: Codec[Price] = phantomCodec[Double, Price.Phantom]

  implicit val placedAtCirceCodec: Codec[PlacedAt] = phantomCodec[LocalDateTime, PlacedAt.Phantom]

  implicit val shippingMethodIDCirceCodec: Codec[ShippingMethodID] = phantomCodec[Int, ShippingMethodID.Phantom]

  implicit val billingFirstNameCirceCodec: Codec[BillingFirstName] = phantomCodec[String, BillingFirstName.Phantom]

  implicit val billingLastNameCirceCodec: Codec[BillingLastName] = phantomCodec[String, BillingLastName.Phantom]

  implicit val billingAddressCirceCodec: Codec[BillingAddress] = phantomCodec[String, BillingAddress.Phantom]

  implicit val billingCityCirceCodec: Codec[BillingCity] = phantomCodec[String, BillingCity.Phantom]

  implicit val billingCountyCirceCodec: Codec[BillingCounty] = phantomCodec[String, BillingCounty.Phantom]

  implicit val billingCountryCirceCodec: Codec[BillingCountry] = phantomCodec[String, BillingCountry.Phantom]

  implicit val billingPostalCodeCirceCodec: Codec[BillingPostalCode] = phantomCodec[String, BillingPostalCode.Phantom]

  implicit val billingPhoneNumberCirceCodec: Codec[BillingPhoneNumber] = phantomCodec[String, BillingPhoneNumber.Phantom]

  implicit val orderTokenCirceCodec: Codec[OrderToken] = phantomCodec[String, OrderToken.Phantom]

  implicit val contentCirceCodec: Codec[EmailContent] = phantomCodec[String, EmailContent.Phantom]

  implicit val fullNameCirceCodec: Codec[FullName] = phantomCodec[String, FullName.Phantom]

  implicit val contactRequestCodec: Codec[ContactRequest] = derive.codec[ContactRequest]

  implicit val buyerCodec: Codec[Buyer] = derive.codec[Buyer]

  implicit val orderedProductDefinitionCodec: Codec[OrderedProductDefinition] =
    derive.codec[OrderedProductDefinition]

  implicit val orderedProductCodec: Codec[OrderedProduct] =
    derive.codec[OrderedProduct]

  implicit val shippingMethodCodec: Codec[ShippingMethod] =
    derive.codec[ShippingMethod]

  implicit val orderDefinitionCodec: Codec[OrderDefinition] =
    derive.codec[OrderDefinition]

  implicit val categoryCodec: Codec[Category] = derive.codec[Category]

  implicit val orderCodec: Codec[Order] = derive.codec[Order]

}
