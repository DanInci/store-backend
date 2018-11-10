package store.service.product.rest

import java.time.LocalDateTime

import store.algebra.content.entity._
import store.json._
import store.algebra.product._
import store.algebra.product.entity._
import store.algebra.product.entity.component._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait ProductServiceJSON extends StoreCoreJSON {

  implicit val formatCirceCodec: Codec[Format] = Codec.instance[Format](
    encode = Encoder.apply[String].contramap(f => f.formatStr),
    decode = Decoder.apply[String].emap(s => Format.fromString(s).left.map(_.message))
  )

  implicit val sexCirceCodec: Codec[Sex] = Codec.instance[Sex](
    encode = Encoder.apply[String].contramap(sex => sex.productPrefix),
    decode = Decoder.apply[String].emap(s => Sex.fromString(s).left.map(_.message))
  )

  implicit val productSizeCirceCodec: Codec[ProductSize] = Codec.instance[ProductSize](
    encode = Encoder.apply[String].contramap(size => size.productPrefix),
    decode = Decoder.apply[String].emap(s => ProductSize.fromString(s).left.map(_.message))
  )

  implicit val countCirceCodec: Codec[Count] = Codec.instance[Count](
    encode = Encoder.apply[Int].contramap(_.count),
    decode = Decoder.apply[Int].emap(Count.apply(_).left.map(_.message))
  )

  implicit val discountCirceCodec: Codec[Discount] = Codec.instance[Discount](
    encode = Encoder.apply[Double].contramap(_.percentage),
    decode = Decoder.apply[Double].emap(Discount(_).left.map(_.message))
  )

  implicit val productIDCirceCodec: Codec[ProductID] = phantomCodec[Long, ProductID.Phantom]

  implicit val categoryIDCirceCodec: Codec[CategoryID] = phantomCodec[Int, CategoryID.Phantom]

  implicit val priceCirceCodec: Codec[Price] = phantomCodec[Double, Price.Phantom]

  implicit val descriptionParagraphCirceCodec: Codec[DescParagraph] = phantomCodec[String, DescParagraph.Phantom]

  implicit val careParagraphCirceCodec: Codec[CareParagraph] = phantomCodec[String, CareParagraph.Phantom]

  implicit val addedAtCirceCodec: Codec[AddedAt] = phantomCodec[LocalDateTime, AddedAt.Phantom]

  implicit val contentCodec: Codec[Content] = derive.codec[Content]

  implicit val categoryCodec: Codec[Category] = derive.codec[Category]

  implicit val stockCodec: Codec[Stock] = derive.codec[Stock]

  implicit val storeProductCodec: Codec[StoreProduct] = derive.codec[StoreProduct]

  implicit val storeProductDefinitionCodec: Codec[StoreProductDefinition] = derive.codec[StoreProductDefinition]

  implicit val productPromotionCodec: Codec[ProductPromotion] = derive.codec[ProductPromotion]

  implicit val productNavigationCodec: Codec[ProductNavigation] = derive.codec[ProductNavigation]

}
