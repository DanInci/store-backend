package store.json

import cats.syntax.contravariant._
import io.circe.syntax._
import shapeless.tag.@@
import store.core.PhantomType

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
private[json] trait StoreJSON {

  implicit def encodeEither[A, B](
      implicit
      encoderA: Encoder[A],
      encoderB: Encoder[B]
  ): Encoder[Either[A, B]] = { o: Either[A, B] =>
    o.fold(_.asJson, _.asJson)
  }

  implicit def decodeEither[A, B](
      implicit
      decoderA: Decoder[A],
      decoderB: Decoder[B]
  ): Decoder[Either[A, B]] = { c: HCursor =>
    c.as[A] match {
      case Right(a) => Right(Left(a))
      case _        => c.as[B].map(Right(_))
    }
  }

  final def phantomCodec[P, T <: PhantomType[P]](implicit enc: Encoder[P], dec: Decoder[P]): Codec[P @@ T] = Codec.instance[P @@ T](
    encode = Encoder.apply[P].narrow,
    decode = Decoder.apply[P].map(shapeless.tag[T](_))
  )

}
