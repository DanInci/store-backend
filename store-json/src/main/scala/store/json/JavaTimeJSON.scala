package store.json

import java.time.LocalDateTime

import store.core.TimeFormatters

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait JavaTimeJSON {

  implicit val localDateCirceCodec: Codec[LocalDateTime] = Codec.instance(
    Encoder.apply[String].contramap(m => m.format(TimeFormatters.LocalDateTimeFormatter)),
    Decoder.apply[String].map(s => LocalDateTime.parse(s, TimeFormatters.LocalDateTimeFormatter))
  )

}
