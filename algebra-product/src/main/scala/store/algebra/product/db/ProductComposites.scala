package store.algebra.product.db

import doobie._
import doobie.postgres.implicits._
import store.algebra.content.ContentID
import store.algebra.product._
import store.algebra.product.entity.component.{Discount, ProductSize}
import store.core._
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
trait ProductComposites {

  implicit val productIDMeta: Meta[ProductID] = Meta[Long].xmap(
    ProductID.apply,
    ProductID.unapply
  )

  implicit val categoryIDMeta: Meta[CategoryID] = Meta[Int].xmap(
    CategoryID.apply,
    CategoryID.unapply
  )

  implicit val contentIDMeta: Meta[ContentID] = Meta[String].xmap(
    ContentID.apply,
    ContentID.unapply
  )

  implicit val pageOffsetMeta: Meta[PageOffset] = Meta[Int].xmap(
    PageOffset.apply,
    PageOffset.unapply
  )

  implicit val pageLimitMeta: Meta[PageLimit] = Meta[Int].xmap(
    PageLimit.apply,
    PageLimit.unapply
  )

  implicit val productSizeMeta: Meta[ProductSize] = Meta[String].xmap(
    s => ProductSize.fromString(s).unsafeGet(),
    ps => ps.productPrefix
  )

  implicit val countMeta: Meta[Count] = Meta[Int].xmap(
    Count.apply,
    Count.unapply
  )

  implicit val priceMeta: Meta[Price] = Meta[Double].xmap(
    Price.apply,
    Price.unapply
  )

  implicit val discountMeta: Meta[Discount] = Meta[Double].xmap(
    d => Discount(d).unsafeGet(),
    _.percentage
  )

  implicit val descParagraphMeta: Meta[DescParagraph] = Meta[String].xmap(
    DescParagraph.apply,
    DescParagraph.unapply
  )

  implicit val descParagraphListMeta: Meta[List[DescParagraph]] = Meta[List[String]].xmap(
    sl => sl.map(DescParagraph.apply),
    dpl => dpl.map(DescParagraph.unapply)
  )

  implicit val careParagraphMeta: Meta[CareParagraph] = Meta[String].xmap(
    CareParagraph.apply,
    CareParagraph.unapply
  )

  implicit val careParagraphListMeta: Meta[List[CareParagraph]] = Meta[List[String]].xmap(
    sl => sl.map(CareParagraph.apply),
    dpl => dpl.map(CareParagraph.unapply)
  )

}
