package store

import busymachines.{json => bmj}

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
package object json extends bmj.JsonTypeDefinitions with bmj.DefaultTypeDiscriminatorConfig {
  type Codec[A] = bmj.Codec[A]
  @inline def Codec: bmj.Codec.type = bmj.Codec
}

