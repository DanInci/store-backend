package store.core

import java.time.format.DateTimeFormatter

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
object TimeFormatters {
  val LocalDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
  val LocalDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm")
}
