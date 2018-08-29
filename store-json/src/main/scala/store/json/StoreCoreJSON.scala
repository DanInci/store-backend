package store.json

import store.core._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait StoreCoreJSON {

  implicit val lLinkCirceCodec: Codec[Link] = Codec.instance[Link](
    encode = Encoder.apply[String].contramap(Link.unapply),
    decode = Decoder.apply[String].map(Link.apply)
  )

}
