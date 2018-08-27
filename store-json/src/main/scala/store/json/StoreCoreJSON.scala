package store.json

import store.core._
import store.core.entity._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait StoreCoreJSON {

  implicit val productIDCirceCodec: Codec[ProductID] = Codec.instance[ProductID](
    encode = Encoder.apply[Long].contramap(ProductID.unapply),
    decode = Decoder.apply[Long].map(ProductID.apply)
  )

  implicit val categoryIDCirceCodec: Codec[CategoryID] = Codec.instance[CategoryID](
    encode = Encoder.apply[Int].contramap(CategoryID.unapply),
    decode = Decoder.apply[Int].map(CategoryID.apply)
  )

  implicit val lLinkCirceCodec: Codec[Link] = Codec.instance[Link](
    encode = Encoder.apply[String].contramap(Link.unapply),
    decode = Decoder.apply[String].map(Link.apply)
  )

  implicit val categoryCodec: Codec[Category] = derive.codec[Category]

}
