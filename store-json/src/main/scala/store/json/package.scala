package store

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
package object json extends Http4sCirceInstances {

  final type Encoder[A] = io.circe.Encoder[A]
  @inline def Encoder: io.circe.Encoder.type = io.circe.Encoder

  final type Decoder[A] = io.circe.Decoder[A]
  @inline def Decoder: io.circe.Decoder.type = io.circe.Decoder

  final type Json = io.circe.Json
  @inline final def Json: io.circe.Json.type = io.circe.Json

  final type HCursor = io.circe.HCursor
  @inline final def HCursor: io.circe.HCursor.type = io.circe.HCursor

}
