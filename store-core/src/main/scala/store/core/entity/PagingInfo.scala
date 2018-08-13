package store.core.entity

import store.core.{PageLimit, PageOffset}

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
object PagingInfo {

  lazy val defaultPageLimit = PageLimit(20)

  lazy val maxPageLimit = PageLimit(50)

  lazy val defaultPagingInfo = new PagingInfo(PageOffset(0), defaultPageLimit)

  def apply(offset: Option[PageOffset], limit: Option[PageLimit]): PagingInfo = {
    val correctedOffset = offset match {
      case Some(o) => if (o < 0) PageOffset(0) else o
      case None    => PageOffset(0)
    }
    val correctedLimit = limit match {
      case Some(l) =>
        if (l < 0) defaultPageLimit else if (l > 50) maxPageLimit else l
      case None => defaultPageLimit
    }
    new PagingInfo(correctedOffset, correctedLimit)
  }
}
final case class PagingInfo private (offset: PageOffset, limit: PageLimit)
