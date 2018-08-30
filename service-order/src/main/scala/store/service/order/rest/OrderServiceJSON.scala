package store.service.order.rest

import java.time.LocalDateTime

import store.algebra.email.Content
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
trait OrderServiceJSON extends StoreJSON {

  implicit val productIDCirceCodec: Codec[ProductID] =
    Codec.instance[ProductID](
      encode = Encoder.apply[Long].contramap(ProductID.unapply),
      decode = Decoder.apply[Long].map(ProductID.apply)
    )

  implicit val categoryIDCirceCodec: Codec[CategoryID] =
    Codec.instance[CategoryID](
      encode = Encoder.apply[Int].contramap(CategoryID.unapply),
      decode = Decoder.apply[Int].map(CategoryID.apply)
    )

  implicit val sexCirceCodec: Codec[Sex] = Codec.instance[Sex](
    encode = Encoder.apply[String].contramap(sex => sex.productPrefix),
    decode =
      Decoder.apply[String].emap(s => Sex.fromString(s).left.map(_.message))
  )

  implicit val orderIDCirceCodec: Codec[OrderID] = Codec.instance[OrderID](
    encode = Encoder.apply[Long].contramap(OrderID.unapply),
    decode = Decoder.apply[Long].map(OrderID.apply)
  )

  implicit val emailCirceCodec: Codec[Email] = Codec.instance[Email](
    encode = Encoder.apply[String].contramap(email => email.emailStr),
    decode = Decoder.apply[String].emap(s => Email(s).left.map(_.message))
  )

  implicit val firstNameCirceCodec: Codec[FirstName] =
    Codec.instance[FirstName](
      encode = Encoder.apply[String].contramap(FirstName.unapply),
      decode = Decoder.apply[String].map(FirstName.apply)
    )

  implicit val lastNameCirceCodec: Codec[LastName] = Codec.instance[LastName](
    encode = Encoder.apply[String].contramap(LastName.unapply),
    decode = Decoder.apply[String].map(LastName.apply)
  )

  implicit val addressCirceCodec: Codec[Address] = Codec.instance[Address](
    encode = Encoder.apply[String].contramap(Address.unapply),
    decode = Decoder.apply[String].map(Address.apply)
  )

  implicit val cityCirceCodec: Codec[City] = Codec.instance[City](
    encode = Encoder.apply[String].contramap(City.unapply),
    decode = Decoder.apply[String].map(City.apply)
  )

  implicit val countyCirceCodec: Codec[County] = Codec.instance[County](
    encode = Encoder.apply[String].contramap(County.unapply),
    decode = Decoder.apply[String].map(County.apply)
  )

  implicit val countryCirceCodec: Codec[Country] = Codec.instance[Country](
    encode = Encoder.apply[String].contramap(Country.unapply),
    decode = Decoder.apply[String].map(Country.apply)
  )

  implicit val postalCodeCirceCodec: Codec[PostalCode] =
    Codec.instance[PostalCode](
      encode = Encoder.apply[String].contramap(PostalCode.unapply),
      decode = Decoder.apply[String].map(PostalCode.apply)
    )

  implicit val phoneNumberCirceCodec: Codec[PhoneNumber] =
    Codec.instance[PhoneNumber](
      encode = Encoder.apply[String].contramap(PhoneNumber.unapply),
      decode = Decoder.apply[String].map(PhoneNumber.apply)
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

  implicit val priceCirceCodec: Codec[Price] = Codec.instance[Price](
    encode = Encoder.apply[Double].contramap(Price.unapply),
    decode = Decoder.apply[Double].map(Price.apply)
  )

  implicit val discountCirceCodec: Codec[Discount] = Codec.instance[Discount](
    encode = Encoder.apply[Double].contramap(_.percentage),
    decode = Decoder.apply[Double].emap(Discount(_).left.map(_.message))
  )

  implicit val placedAtCirceCodec: Codec[PlacedAt] = Codec.instance[PlacedAt](
    encode = Encoder.apply[LocalDateTime].contramap(PlacedAt.unapply),
    decode = Decoder.apply[LocalDateTime].map(PlacedAt.apply)
  )

  implicit val shippingMethodIDCirceCodec: Codec[ShippingMethodID] =
    Codec.instance[ShippingMethodID](
      encode = Encoder.apply[Int].contramap(ShippingMethodID.unapply),
      decode = Decoder.apply[Int].map(ShippingMethodID.apply)
    )

  implicit val billingFirstNameCirceCodec: Codec[BillingFirstName] =
    Codec.instance[BillingFirstName](
      encode = Encoder.apply[String].contramap(BillingFirstName.unapply),
      decode = Decoder.apply[String].map(BillingFirstName.apply)
    )

  implicit val billingLastNameCirceCodec: Codec[BillingLastName] =
    Codec.instance[BillingLastName](
      encode = Encoder.apply[String].contramap(BillingLastName.unapply),
      decode = Decoder.apply[String].map(BillingLastName.apply)
    )

  implicit val billingAddressCirceCodec: Codec[BillingAddress] =
    Codec.instance[BillingAddress](
      encode = Encoder.apply[String].contramap(BillingAddress.unapply),
      decode = Decoder.apply[String].map(BillingAddress.apply)
    )

  implicit val billingCityCirceCodec: Codec[BillingCity] =
    Codec.instance[BillingCity](
      encode = Encoder.apply[String].contramap(BillingCity.unapply),
      decode = Decoder.apply[String].map(BillingCity.apply)
    )

  implicit val billingCountyCirceCodec: Codec[BillingCounty] =
    Codec.instance[BillingCounty](
      encode = Encoder.apply[String].contramap(BillingCounty.unapply),
      decode = Decoder.apply[String].map(BillingCounty.apply)
    )

  implicit val billingCountryCirceCodec: Codec[BillingCountry] =
    Codec.instance[BillingCountry](
      encode = Encoder.apply[String].contramap(BillingCountry.unapply),
      decode = Decoder.apply[String].map(BillingCountry.apply)
    )

  implicit val billingPostalCodeCirceCodec: Codec[BillingPostalCode] =
    Codec.instance[BillingPostalCode](
      encode = Encoder.apply[String].contramap(BillingPostalCode.unapply),
      decode = Decoder.apply[String].map(BillingPostalCode.apply)
    )

  implicit val billingPhoneNumberCirceCodec: Codec[BillingPhoneNumber] =
    Codec.instance[BillingPhoneNumber](
      encode = Encoder.apply[String].contramap(BillingPhoneNumber.unapply),
      decode = Decoder.apply[String].map(BillingPhoneNumber.apply)
    )

  implicit val orderTokenCirceCodec: Codec[OrderToken] = Codec.instance[OrderToken](
    encode = Encoder.apply[String].contramap(OrderToken.unapply),
    decode = Decoder.apply[String].map(OrderToken.apply)
  )

  implicit val contentCirceCodec: Codec[Content] = Codec.instance[Content](
    encode = Encoder.apply[String].contramap(Content.unapply),
    decode = Decoder.apply[String].map(Content.apply)
  )

  implicit val fullNameCirceCodec: Codec[FullName] = Codec.instance[FullName](
    encode = Encoder.apply[String].contramap(FullName.unapply),
    decode = Decoder.apply[String].map(FullName.apply)
  )

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
