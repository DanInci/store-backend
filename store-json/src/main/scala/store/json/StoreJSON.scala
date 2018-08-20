package store.json

import busymachines.json.AnomalyJsonCodec

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 04/08/2018
  */
trait StoreJSON extends StoreCoreJSON with JavaTimeJSON with AnomalyJsonCodec
