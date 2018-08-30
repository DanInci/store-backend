package store.algebra.order.db

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import store.effects._
import doobie._
import store.algebra.order._
import store.algebra.product._
import store.algebra.product.entity.component._
import store.core._
import store.core.entity.Email

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
trait OrderComposites {

  implicit val orderIDMeta: Meta[OrderID] = Meta[Long].xmap(
    OrderID.apply,
    OrderID.unapply
  )

  implicit val orderedProductIDMeta: Meta[OrderedProductID] = Meta[Long].xmap(
    OrderedProductID.apply,
    OrderedProductID.unapply
  )

  implicit val buyerIDMeta: Meta[BuyerID] = Meta[Long].xmap(
    BuyerID.apply,
    BuyerID.unapply
  )

  implicit val productIDMeta: Meta[ProductID] = Meta[Long].xmap(
    ProductID.apply,
    ProductID.unapply
  )

  implicit val categoryIDMeta: Meta[CategoryID] = Meta[Int].xmap(
    CategoryID.apply,
    CategoryID.unapply
  )

  implicit val sexMeta: Meta[Sex] = Meta[String].xmap(
    Sex.fromString(_).unsafeGet(),
    _.productPrefix
  )

  implicit val productSizeMeta: Meta[ProductSize] = Meta[String].xmap(
    ProductSize.fromString(_).unsafeGet(),
    _.productPrefix
  )

  implicit val countMeta: Meta[Count] = Meta[Int].xmap(
    Count.apply(_).unsafeGet(),
    _.count
  )

  implicit val priceMeta: Meta[Price] = Meta[Double].xmap(
    Price.apply,
    Price.unapply
  )

  implicit val discountMeta: Meta[Discount] = Meta[Double].xmap(
    d => Discount(d).unsafeGet(),
    _.percentage
  )

  implicit val emailMeta: Meta[Email] = Meta[String].xmap(
    s => Email.apply(s).unsafeGet(),
    _.emailStr
  )

  implicit val firstNameMeta: Meta[FirstName] = Meta[String].xmap(
    FirstName.apply,
    FirstName.unapply
  )

  implicit val lastNameMeta: Meta[LastName] = Meta[String].xmap(
    LastName.apply,
    LastName.unapply
  )

  implicit val addressMeta: Meta[Address] = Meta[String].xmap(
    Address.apply,
    Address.unapply
  )

  implicit val cityMeta: Meta[City] = Meta[String].xmap(
    City.apply,
    City.unapply
  )

  implicit val countyMeta: Meta[County] = Meta[String].xmap(
    County.apply,
    County.unapply
  )

  implicit val countryMeta: Meta[Country] = Meta[String].xmap(
    Country.apply,
    Country.unapply
  )

  implicit val postalCodeMeta: Meta[PostalCode] = Meta[String].xmap(
    PostalCode.apply,
    PostalCode.unapply
  )

  implicit val phoneNumberMeta: Meta[PhoneNumber] = Meta[String].xmap(
    PhoneNumber.apply,
    PhoneNumber.unapply
  )

  implicit val localDateMeta: Meta[LocalDate] = Meta[Timestamp].xmap(
    _.toLocalDateTime.toLocalDate,
    t => Timestamp.valueOf(t.atStartOfDay())
  )

  implicit val localDateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].xmap(
    _.toLocalDateTime,
    Timestamp.valueOf
  )

  implicit val placedAtMeta: Meta[PlacedAt] = Meta[LocalDateTime].xmap(
    PlacedAt.apply,
    PlacedAt.unapply
  )

  implicit val startDateMeta: Meta[StartDate] = Meta[LocalDate].xmap(
    StartDate.apply,
    StartDate.unapply
  )

  implicit val endDateMeta: Meta[EndDate] = Meta[LocalDate].xmap(
    EndDate.apply,
    EndDate.unapply
  )

  implicit val shippingMethodIDMeta: Meta[ShippingMethodID] = Meta[Int].xmap(
    ShippingMethodID.apply,
    ShippingMethodID.unapply
  )

  implicit val billingFirstNameMeta: Meta[BillingFirstName] = Meta[String].xmap(
    BillingFirstName.apply,
    BillingFirstName.unapply
  )

  implicit val billingLastNameMeta: Meta[BillingLastName] = Meta[String].xmap(
    BillingLastName.apply,
    BillingLastName.unapply
  )

  implicit val billingAddressMeta: Meta[BillingAddress] = Meta[String].xmap(
    BillingAddress.apply,
    BillingAddress.unapply
  )

  implicit val billingCityMeta: Meta[BillingCity] = Meta[String].xmap(
    BillingCity.apply,
    BillingCity.unapply
  )

  implicit val billingCountyMeta: Meta[BillingCounty] = Meta[String].xmap(
    BillingCounty.apply,
    BillingCounty.unapply
  )

  implicit val billingCountryMeta: Meta[BillingCountry] = Meta[String].xmap(
    BillingCountry.apply,
    BillingCountry.unapply
  )

  implicit val billingPostalCodeMeta: Meta[BillingPostalCode] =
    Meta[String].xmap(
      BillingPostalCode.apply,
      BillingPostalCode.unapply
    )

  implicit val billingPhoneNumberMeta: Meta[BillingPhoneNumber] =
    Meta[String].xmap(
      BillingPhoneNumber.apply,
      BillingPhoneNumber.unapply
    )

  implicit val orderTokenMeta: Meta[OrderToken] = Meta[String].xmap(
    OrderToken.apply,
    OrderToken.unapply
  )

  implicit val pageOffsetMeta: Meta[PageOffset] = Meta[Int].xmap(
    PageOffset.apply,
    PageOffset.unapply
  )

  implicit val pageLimitMeta: Meta[PageLimit] = Meta[Int].xmap(
    PageLimit.apply,
    PageLimit.unapply
  )

}
