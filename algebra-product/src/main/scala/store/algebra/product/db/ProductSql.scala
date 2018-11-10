package store.algebra.product.db

import java.time.LocalDateTime

import store.core._
import doobie._
import doobie.implicits._
import cats.implicits._
import store.algebra.content._
import store.algebra.product._
import store.algebra.product.db.entity._
import store.algebra.product.entity._
import store.algebra.product.entity.component._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
object ProductSql extends ProductComposites {

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

  def mapContentToProduct(contentId: ContentID,
                          productId: ProductID): ConnectionIO[Int] =
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

  def insertProduct(definition: StoreProductDefinition): ConnectionIO[ProductID] =
    AsyncConnectionIO.delay(LocalDateTime.now).flatMap { now =>
      sql"""INSERT INTO product (c_category_id, name, price, discount, is_on_promotion, availability_on_command, description, care, added_at)
         | VALUES (${definition.categoryId},${definition.name},${definition.price},${definition.discount},${definition.isOnPromotion},${definition.isAvailableOnCommand},${definition.description}, ${definition.care}, $now)""".stripMargin.update
        .withUniqueGeneratedKeys("product_id")
    }

  def updateProductPromotion(productId: ProductID, isOnPromotion: Boolean, promotionImage: Option[ContentID]): ConnectionIO[Int] =
    sql"UPDATE product SET is_on_promotion=$isOnPromotion, c_promotion_image=$promotionImage WHERE product_id=$productId".update.run

  def findById(productId: ProductID): ConnectionIO[Option[StoreProductDB]] =
    sql"""SELECT p.product_id, p.name, p.price, p.discount, p.is_on_promotion, co.content_id, co.name, co.format, p.availability_on_command, p.description, p.care, p.added_at, ca.category_id, ca.name, ca.sex
         | FROM product p
         | INNER JOIN category ca ON p.c_category_id = ca.category_id
         | LEFT JOIN content co ON p.c_promotion_image = co.content_id
         | WHERE p.product_id=$productId""".stripMargin
      .query[StoreProductDB]
      .option

  def findNextByCurrentId(currentProductId: ProductID): ConnectionIO[Option[StoreProductDB]] =
    sql"""SELECT p.product_id, p.name, p.price, p.discount, p.is_on_promotion, co.content_id, co.name, co.format, p.availability_on_command, p.description, p.care, p.added_at, ca.category_id, ca.name, ca.sex
         | FROM product p
         | INNER JOIN category ca ON p.c_category_id = ca.category_id
         | LEFT JOIN content co ON p.c_promotion_image = co.content_id
         | WHERE p.product_id > $currentProductId
         | ORDER BY p.product_id ASC
         | LIMIT 1""".stripMargin
      .query[StoreProductDB]
      .option

  def findPreviousByCurrentId(currentProductId: ProductID): ConnectionIO[Option[StoreProductDB]] =
    sql"""SELECT p.product_id, p.name, p.price, p.discount, p.is_on_promotion, co.content_id, co.name, co.format, p.availability_on_command, p.description, p.care, p.added_at, ca.category_id, ca.name, ca.sex
         | FROM product p
         | INNER JOIN category ca ON p.c_category_id = ca.category_id
         | LEFT JOIN content co ON p.c_promotion_image = co.content_id
         | WHERE p.product_id < $currentProductId
         | ORDER BY p.product_id DESC
         | LIMIT 1""".stripMargin
      .query[StoreProductDB]
      .option

  def findByNameAndCategoriesOrderedByAddedAtDesc(
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

    (sql"""SELECT p.product_id, p.name, p.price, p.discount, p.is_on_promotion, co.content_id, co.name, co.format, p.availability_on_command, p.description, p.care, p.added_at, ca.category_id, ca.name, ca.sex
         | FROM product p
         | INNER JOIN category ca ON p.c_category_id = ca.category_id
         | LEFT JOIN content co ON p.c_promotion_image = co.content_id """.stripMargin
      ++ Fragment.const(whereClause) ++
      sql""" ORDER BY p.added_at DESC
           | LIMIT $limit OFFSET ${offset * limit}""".stripMargin)
      .query[StoreProductDB]
      .to[List]
  }

  def findAllProductsAddedBetween(startDate: LocalDateTime, endDate: LocalDateTime): ConnectionIO[List[StoreProductDB]] =
    sql"""SELECT p.product_id, p.name, p.price, p.discount, p.is_on_promotion, co.content_id, co.name, co.format, p.availability_on_command, p.description, p.care, p.added_at, ca.category_id, ca.name, ca.sex
         | FROM product p
         | INNER JOIN category ca ON p.c_category_id = ca.category_id
         | LEFT JOIN content co ON p.c_promotion_image = co.content_id
         | WHERE p.added_at BETWEEN $startDate AND $endDate""".stripMargin.query[StoreProductDB].to[List]

  def deleteProduct(productId: ProductID): ConnectionIO[Int] =
    sql"DELETE FROM product WHERE product_id=$productId".update.run

  // STOCKS QUERIES

  def addStockToProduct(stock: Stock, productId: ProductID): ConnectionIO[Int] =
    sql"INSERT INTO stock (p_product_id, product_size, available_count) VALUES ($productId, ${stock.size}, ${stock.count})".update.run

  def findStocksByProductID(productId: ProductID): ConnectionIO[List[Stock]] =
    for {
      stocks <- sql"SELECT product_size, available_count FROM stock WHERE p_product_id=$productId"
        .query[Stock]
        .to[List]
      sortedStocks = stocks.sortBy(_.size)(ProductSize.productSizeOrdering)
    } yield sortedStocks

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
}
