package store.json

import store.core._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
trait StoreCoreJSON extends StoreJSON with JavaTimeJSON{

  implicit val titleCirceCodec: Codec[Title] = phantomCodec[String, Title.Phantom]

  implicit val descriptionCirceCodec: Codec[Description] = phantomCodec[String, Description.Phantom]

}
