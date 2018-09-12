package store.json

import io.circe.syntax._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait StoreJSON extends StoreCoreJSON with JavaTimeJSON {

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

}
