package store.algebra.product.db

import store.core._
import doobie._
import doobie.implicits._
import cats.implicits._
import store.algebra.content._
import store.algebra.content.entity._
import store.algebra.product._
import store.algebra.product.db.entity._
import store.algebra.product.entity._
import store.algebra.product.entity.component._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
object ProductSql extends ProductComposites {

  final case class ContentDB(
      contentId: ContentID,
      name: String,
      format: Format
  )

  final case class PromotionDB(
      promotionId: PromotionID,
      title: Title,
      description: Description,
      promotedProductId: Option[ProductID],
      content: ContentDB,
      expiresAt: PromotionExpiration
  )

  // CATEGORY QUERIES

  def findCategoriesBySex(sex: Sex): ConnectionIO[List[CategoryDB]] = {
    sql"SELECT category_id, name, sex FROM category WHERE sex=$sex"
      .query[CategoryDB]
      .to[List]
  }

  // CONTENT QUERIES
  def insertContent(contentDB: ContentDB): ConnectionIO[Int] =
    sql"INSERT INTO content (content_id, name, format) VALUES (${contentDB.contentId}, ${contentDB.name}, ${contentDB.format})".update.run

  def findContentByID(id: ContentID): ConnectionIO[Option[ContentDB]] =
    sql"SELECT content_id, name, format FROM content WHERE content_id=$id"
      .query[ContentDB]
      .option

  def findContentsByProductID(
      productId: ProductID): ConnectionIO[List[ContentDB]] = {
    sql"""SELECT c.content_id, c.name, c.format
         | FROM content c
         | INNER JOIN product_content_map pcmap ON c.content_id = pcmap.c_content_id
         | WHERE pcmap.p_product_id=$productId""".stripMargin
      .query[ContentDB]
      .to[List]
  }

  def mapContentToProduct(contentId: ContentID, productId: ProductID): ConnectionIO[Int] =
    sql"INSERT INTO product_content_map (p_product_id, c_content_id) VALUES ($productId, $contentId)".update.run

  def deleteContentsByProductID(productId: ProductID): ConnectionIO[Unit] =
    for {
      contentDBs <- findContentsByProductID(productId)
      _ <- sql"DELETE FROM product_content_map WHERE p_product_id=$productId".update.run
      _ <- contentDBs.map(c => deleteContentByID(c.contentId)).sequence
    } yield ()

  def deleteContentByID(contentId: ContentID): ConnectionIO[Int] =
    sql"DELETE FROM content WHERE content_id=$contentId".update.run

  // PRODUCT QUERIES

  def insertProduct(
      definition: StoreProductDefinition): ConnectionIO[ProductID] = {
    sql"""INSERT INTO product (c_category_id, name, price, discount, availability_on_command, description, care)
         | VALUES (${definition.categoryId},${definition.name},${definition.price},${definition.discount},${definition.isAvailableOnCommand},${definition.description}, ${definition.care})""".stripMargin.update
      .withUniqueGeneratedKeys("product_id")
  }

  def findById(productId: ProductID): ConnectionIO[Option[StoreProductDB]] =
    sql"""SELECT p.product_id, p.name, p.price, p.discount, p.availability_on_command, p.description, p.care, c.category_id, c.name, c.sex
         | FROM product p
         | INNER JOIN category c ON p.c_category_id = c.category_id
         | WHERE p.product_id=$productId""".stripMargin
      .query[StoreProductDB]
      .option

  def findByNameAndCategories(
      name: Option[String],
      categories: List[CategoryID],
      offset: PageOffset,
      limit: PageLimit): ConnectionIO[List[StoreProductDB]] = {
    val whereClause = {
      val nameFilter = name.map(n => s"UPPER(p.name) LIKE UPPER('$n%')")
      val categoriesFilter = categories match {
        case Nil => None
        case c =>
          Some(
            s"c.category_id IN ${c.map(v => s"'$v'").mkString_("(", ",", ")")}")
      }

      (nameFilter, categoriesFilter) match {
        case (Some(n), Some(c)) => s"WHERE $n AND $c"
        case (Some(n), None)    => s"WHERE $n"
        case (None, Some(c))    => s"WHERE $c"
        case (None, None)       => ""
      }
    }

    (sql"""SELECT p.product_id, p.name, p.price, p.discount, p.availability_on_command, p.description, p.care, c.category_id, c.name, c.sex
         | FROM product p
         | INNER JOIN category c ON p.c_category_id = c.category_id """.stripMargin
      ++ Fragment.const(whereClause) ++
      sql" LIMIT $limit OFFSET ${offset * limit}")
      .query[StoreProductDB]
      .to[List]

  }

  def deleteProduct(productId: ProductID): ConnectionIO[Int] =
    sql"DELETE FROM product WHERE product_id=$productId".update.run

  // STOCKS QUERIES

  def addStockToProduct(stock: Stock, productId: ProductID): ConnectionIO[Int] =
    sql"INSERT INTO stock (p_product_id, product_size, available_count) VALUES ($productId, ${stock.size}, ${stock.count})".update.run

  def findStocksByProductID(productId: ProductID): ConnectionIO[List[Stock]] =
    sql"SELECT product_size, available_count FROM stock WHERE p_product_id=$productId"
      .query[Stock]
      .to[List]

  def findStockByProductIdAndSize(
      productId: ProductID,
      productSize: ProductSize): ConnectionIO[Option[Stock]] = {
    sql"SELECT product_size, available_count FROM stock WHERE p_product_id=$productId AND product_size=$productSize"
      .query[Stock]
      .option
  }

  def updateStockByProductID(count: Count,
                             productId: ProductID): ConnectionIO[Int] =
    sql"UPDATE stock SET available_count=$count WHERE p_product_id=$productId".update.run

  def updateStockByProductIDAndSize(count: Count,
                                    productId: ProductID,
                                    size: ProductSize): ConnectionIO[Int] =
    sql"UPDATE stock SET available_count=$count WHERE p_product_id=$productId AND product_size=$size".update.run

  def deleteStockByProductID(productId: ProductID): ConnectionIO[Int] =
    sql"DELETE FROM stock WHERE p_product_id=$productId".update.run

  def deleteStockByProductIDAndSize(productId: ProductID,
                                    size: ProductSize): ConnectionIO[Int] =
    sql"DELETE FROM stock WHERE p_product_id=$productId AND product_size=$size".update.run

  // PROMOTION QUERIES
  def insertPromotion(definition: PromotionDefinition,
                      contentId: ContentID): ConnectionIO[PromotionID] =
    sql"""INSERT INTO promotion (title, description, p_product_id, c_content_id, expires_at)
         | VALUES (${definition.title}, ${definition.description}, ${definition.promotedProductId}, $contentId, ${definition.expiresAt})""".stripMargin.update
      .withUniqueGeneratedKeys("promotion_id")

  def findPromotionById(id: PromotionID): ConnectionIO[Option[PromotionDB]] =
    sql"""SELECT p.promotion_id, p.title, p.description, p.p_product_id, c.content_id, c.name, c.format, p.expires_at
         | FROM promotion p
         | INNER JOIN content c on p.c_content_id = c.content_id
         | WHERE p.promotion_id=$id""".stripMargin.query[PromotionDB].option

  def findAllPromotionsOrderedByExpiresAtAsc: ConnectionIO[List[PromotionDB]] =
    sql"""SELECT p.promotion_id, p.title, p.description, p.p_product_id, c.content_id, c.name, c.format, p.expires_at
         | FROM promotion p
         | INNER JOIN content c on p.c_content_id = c.content_id
         | ORDER BY p.expires_at ASC""".stripMargin
      .query[PromotionDB]
      .to[List]

  def deletePromotionById(id: PromotionID): ConnectionIO[Int] =
    sql"DELETE FROM promotion WHERE promotion_id=$id".update.run
}
