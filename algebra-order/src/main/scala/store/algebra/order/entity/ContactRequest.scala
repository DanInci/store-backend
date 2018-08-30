package store.algebra.order.entity

import store.algebra.email.Content
import store.algebra.order._
import store.core.entity.Email

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 30/08/2018
  */
final case class ContactRequest(
    name: FullName,
    email: Email,
    phoneNumber: PhoneNumber,
    message: Content
)
