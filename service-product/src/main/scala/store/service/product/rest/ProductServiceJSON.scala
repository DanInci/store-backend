package store.service.product.rest

import store.algebra.files.BinaryContent
import store.json._
import io.circe.generic.semiauto._
import store.algebra.files.entity.ImageFile
import store.algebra.product.entity.StoreProduct
import store.core.ProductID

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
object ProductServiceJSON extends ProductServiceJSON

trait ProductServiceJSON extends StoreJSON {

  implicit val binaryContentEncoder: Encoder[BinaryContent] = Encoder.apply[Array[Byte]].contramap(BinaryContent.unapply)

  implicit val binaryContentDecoder: Decoder[BinaryContent] = Decoder.apply[Array[Byte]].map(BinaryContent.apply)

  implicit val productIDEncoder: Encoder[ProductID] = Encoder.apply[Long].contramap(ProductID.unapply)

  implicit val productIDDecoder: Decoder[ProductID] = Decoder.apply[Long].map(ProductID.apply)

  implicit val imageFileEncoder: Encoder[ImageFile] = deriveEncoder[ImageFile]

  implicit val imageFileDecoder: Decoder[ImageFile] = deriveDecoder[ImageFile]

  implicit val productEncoder: Encoder[StoreProduct] = deriveEncoder[StoreProduct]

  implicit val productDecoder: Decoder[StoreProduct] = deriveDecoder[StoreProduct]

}
