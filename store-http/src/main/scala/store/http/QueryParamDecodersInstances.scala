package store.http

import org.http4s.QueryParamDecoder
import store.core._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
trait QueryParamDecodersInstances {

  implicit val pageOffsetQueryParamDecoder: QueryParamDecoder[PageOffset] =
    QueryParamDecoder.intQueryParamDecoder.map(PageOffset.apply)

  implicit val limitOffsetQueryParamDecoder: QueryParamDecoder[PageLimit] =
    QueryParamDecoder.intQueryParamDecoder.map(PageLimit.apply)
}
