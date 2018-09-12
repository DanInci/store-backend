package store.json

import store.core._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait StoreCoreJSON {

  implicit val titleCirceCodec: Codec[Title] = Codec.instance[Title](
    encode = Encoder.apply[String].contramap(Title.unapply),
    decode = Decoder.apply[String].map(Title.apply)
  )

  implicit val descriptionCirceCodec: Codec[Description] = Codec.instance[Description](
    encode = Encoder.apply[String].contramap(Description.unapply),
    decode = Decoder.apply[String].map(Description.apply)
  )

}
