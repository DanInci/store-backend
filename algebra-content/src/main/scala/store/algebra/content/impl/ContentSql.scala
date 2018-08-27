package store.algebra.content.impl

import doobie._
import doobie.implicits._
import store.algebra.content.{ContentID, Format}
import store.algebra.content.entity.ContentDB
import store.core.ProductID

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 27/08/2018
  */
object ContentSql {

  implicit val contentIDMeta: Meta[ContentID] = Meta[String].xmap(
    ContentID.apply,
    ContentID.unapply
  )

  implicit val formatMeta: Meta[Format] = Meta[String].xmap(
    Format.apply,
    Format.unapply
  )

  implicit val productIDMeta: Meta[ProductID] = Meta[Long].xmap(
    ProductID.apply,
    ProductID.unapply
  )

  // CONTENT QUERIES

  def addContentToProduct(contentDB: ContentDB,
                          productId: ProductID): ConnectionIO[Int] =
    sql"INSERT INTO content (content_id, p_product_id, name) VALUES (${contentDB.contentId}, $productId, ${contentDB.name})".update.run

  def findContentByProductID(productId: ProductID,
                             format: Format): ConnectionIO[List[ContentDB]] = {
    val whereClause = s"WHERE p_product_id=$productId AND content_id LIKE '%$format'"
    (sql"SELECT content_id, name FROM content " ++ Fragment.const(whereClause))
      .query[ContentDB]
      .to[List]
  }

  def deleteContentByProductID(productId: ProductID): ConnectionIO[Int] =
    sql"DELETE FROM content WHERE p_product_id=$productId".update.run

}
