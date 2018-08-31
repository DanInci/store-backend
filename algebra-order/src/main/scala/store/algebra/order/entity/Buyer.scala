package store.algebra.order.entity

import store.algebra.order._
import store.core.entity.Email

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 18/08/2018
  */
final case class Buyer(
    email: Email,
    isSubscribed: Boolean,
    firstName: FirstName,
    lastName: LastName,
    address: Address,
    city: City,
    county: County,
    country: Country,
    postalCode: PostalCode,
    phoneNumber: Option[PhoneNumber]
) extends Serializable
