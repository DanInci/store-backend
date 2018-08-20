package store.service.product.rest

import store.algebra.content.BinaryContent
import store.json._
import store.algebra.product._
import store.algebra.product.entity._
import store.algebra.product.entity.component._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ProductServiceJSON extends StoreJSON {

  implicit val productSizeCirceCodec: Codec[ProductSize] = Codec.instance[ProductSize](
    encode = Encoder.apply[String].contramap(size => size.productPrefix),
    decode = Decoder.apply[String].emap(s => ProductSize.fromString(s).left.map(_.message))
  )

  implicit val countCirceCodec: Codec[Count] = Codec.instance[Count](
    encode = Encoder.apply[Int].contramap(Count.unapply),
    decode = Decoder.apply[Int].map(Count.apply)
  )

  implicit val priceCirceCodec: Codec[Price] = Codec.instance[Price](
    encode = Encoder.apply[Double].contramap(Price.unapply),
    decode = Decoder.apply[Double].map(Price.apply)
  )

  implicit val discountCirceCodec: Codec[Discount] = Codec.instance[Discount](
    encode = Encoder.apply[Double].contramap(_.percentage),
    decode = Decoder.apply[Double].emap(Discount(_).left.map(_.message))
  )

  implicit val binaryContentCirceCodec: Codec[BinaryContent] = Codec.instance[BinaryContent](
    encode = Encoder.apply[Array[Byte]].contramap(BinaryContent.unapply),
    decode = Decoder.apply[Array[Byte]].map(BinaryContent.apply)
  )

  implicit val descriptionParagraphCirceCodec: Codec[DescParagraph] = Codec.instance[DescParagraph](
    encode = Encoder.apply[String].contramap(DescParagraph.unapply),
    decode = Decoder.apply[String].map(DescParagraph.apply)
  )

  implicit val careParagraphCirceCodec: Codec[CareParagraph] = Codec.instance[CareParagraph](
    encode = Encoder.apply[String].contramap(CareParagraph.unapply),
    decode = Decoder.apply[String].map(CareParagraph.apply)
  )

  implicit val imageFileCodec: Codec[ImageFile] = derive.codec[ImageFile]

  implicit val stockCodec: Codec[Stock] = derive.codec[Stock]

  implicit val storeProductCodec: Codec[StoreProduct] = derive.codec[StoreProduct]

  implicit val storeProductDefinitionCodec: Codec[StoreProductDefinition] = derive.codec[StoreProductDefinition]

}
