package store.algebra.product.db

import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

  def findCategories: ConnectionIO[List[CategoryDB]] =
    sql"SELECT category_id, name, sex FROM category".query[CategoryDB].to[List]

  def findCategoriesBySex(sex: Sex): ConnectionIO[List[CategoryDB]] =
    sql"SELECT category_id, name, sex FROM category WHERE sex=$sex"
      .query[CategoryDB]
      .to[List]

  def findCategoryById(id: CategoryID): ConnectionIO[Option[CategoryDB]] =
    sql"SELECT category_id, name, sex FROM category WHERE category_id=$id"
      .query[CategoryDB]
      .option

  def insertCategory(definition: CategoryDefinition): ConnectionIO[CategoryID] =
    sql"INSERT INTO category (name, sex) VALUES (${definition.name}, ${definition.sex})".update
      .withUniqueGeneratedKeys[CategoryID]("category_id")

  def deleteCategory(categoryId: CategoryID): ConnectionIO[Int] =
    sql"DELETE FROM category WHERE category_id=$categoryId".update.run

  // CONTENT QUERIES
  def insertContent(contentDB: ContentDB): ConnectionIO[Int] =
    sql"INSERT INTO content (content_id, name, format, has_thumbnail, is_promotion_image) VALUES (${contentDB.contentId}, ${contentDB.name}, ${contentDB.format}, ${contentDB.hasThumbnail}, ${contentDB.isPromotionImage})".update.run

  def findContentByID(id: ContentID): ConnectionIO[Option[ContentDB]] =
    sql"SELECT content_id, name, format, has_thumbnail, is_promotion_image FROM content WHERE content_id=$id"
      .query[ContentDB]
      .option

  def findPromotionContent: ConnectionIO[List[ContentDB]] =
    sql"SELECT content_id, name, format, has_thumbnail, is_promotion_image FROM content WHERE is_promotion_image=true"
      .query[ContentDB]
      .to[List]

  def findContentsByProductID(
      productId: ProductID): ConnectionIO[List[ContentDB]] = {
    sql"""SELECT c.content_id, c.name, c.format, c.has_thumbnail, c.is_promotion_image
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

  def insertProduct(
      definition: StoreProductDefinition): ConnectionIO[ProductID] =
    AsyncConnectionIO.delay(LocalDateTime.now).flatMap { now =>
      sql"""INSERT INTO product (c_category_id, name, price, discount, is_available_on_command, is_favourite, description, care, added_at)
         | VALUES (${definition.categoryId},${definition.name},${definition.price},${definition.discount},${definition.isAvailableOnCommand},${definition.isFavourite},${definition.description}, ${definition.care}, $now)""".stripMargin.update
        .withUniqueGeneratedKeys("product_id")
    }

  def updateProductById(productId: ProductID,
                        updates: StoreProductDefinition): ConnectionIO[Int] =
    sql"UPDATE product SET c_category_id=${updates.categoryId}, name=${updates.name}, price=${updates.price}, discount=${updates.discount}, is_available_on_command=${updates.isAvailableOnCommand}, is_favourite=${updates.isFavourite}, description=${updates.description}, care=${updates.care} WHERE product_id=$productId".update.run

  def findById(productId: ProductID): ConnectionIO[Option[StoreProductDB]] =
    sql"""SELECT p.product_id, p.name, p.price, p.discount, p.is_available_on_command, p.is_favourite, p.description, p.care, p.added_at, ca.category_id, ca.name, ca.sex
         | FROM product p
         | INNER JOIN category ca ON p.c_category_id = ca.category_id
         | WHERE p.product_id=$productId""".stripMargin
      .query[StoreProductDB]
      .option

  def findNextByAddedAt(
      addedAt: LocalDateTime,
      name: Option[String],
      age: Option[Int],
      isFavourite: Option[Boolean],
      categories: List[CategoryID]): ConnectionIO[Option[StoreProductDB]] = {
    val whereClause =
      getWhereClause(name, age, isFavourite, None, Some(addedAt), categories)
    (sql"""SELECT p.product_id, p.name, p.price, p.discount, p.is_available_on_command, p.is_favourite, p.description, p.care, p.added_at, ca.category_id, ca.name, ca.sex
          | FROM product p
          | INNER JOIN category ca ON p.c_category_id = ca.category_id """.stripMargin
      ++ Fragment.const(whereClause) ++
      sql"""| ORDER BY p.added_at DESC
            | LIMIT 1""".stripMargin)
      .query[StoreProductDB]
      .option
  }

  def findPreviousByAddedAt(
      addedAt: LocalDateTime,
      name: Option[String],
      age: Option[Int],
      isFavourite: Option[Boolean],
      categories: List[CategoryID]): ConnectionIO[Option[StoreProductDB]] = {
    val whereClause =
      getWhereClause(name, age, isFavourite, Some(addedAt), None, categories)
    (sql"""SELECT p.product_id, p.name, p.price, p.discount, p.is_available_on_command, p.is_favourite, p.description, p.care, p.added_at, ca.category_id, ca.name, ca.sex
         | FROM product p
         | INNER JOIN category ca ON p.c_category_id = ca.category_id """.stripMargin
      ++ Fragment.const(whereClause) ++
      sql"""| ORDER BY p.added_at ASC
               | LIMIT 1""".stripMargin)
      .query[StoreProductDB]
      .option
  }

  def findProductsOrderedByAddedAtDesc(
      name: Option[String],
      age: Option[Int],
      isFavourite: Option[Boolean],
      categories: List[CategoryID],
      offset: PageOffset,
      limit: PageLimit): ConnectionIO[List[StoreProductDB]] = {
    val whereClause =
      getWhereClause(name, age, isFavourite, None, None, categories)

    (sql"""SELECT p.product_id, p.name, p.price, p.discount, p.is_available_on_command, p.is_favourite, p.description, p.care, p.added_at, ca.category_id, ca.name, ca.sex
          | FROM product p
          | INNER JOIN category ca ON p.c_category_id = ca.category_id """.stripMargin
      ++ Fragment.const(whereClause) ++
      sql""" ORDER BY p.added_at DESC
           | LIMIT $limit OFFSET ${offset * limit}""".stripMargin)
      .query[StoreProductDB]
      .to[List]
  }

  def countProducts(name: Option[String],
                    age: Option[Int],
                    isFavourite: Option[Boolean],
                    categories: List[CategoryID]): ConnectionIO[Count] = {
    val whereClause =
      getWhereClause(name, age, isFavourite, None, None, categories)
    (sql"""SELECT COUNT(*)
          | FROM product p
          | INNER JOIN category ca ON p.c_category_id = ca.category_id """.stripMargin
      ++ Fragment.const(whereClause))
      .query[Count]
      .unique
  }

  def findAllProductsAddedBetween(
      startDate: LocalDateTime,
      endDate: LocalDateTime): ConnectionIO[List[StoreProductDB]] =
    sql"""SELECT p.product_id, p.name, p.price, p.discount, p.is_available_on_command, p.is_favourite, p.description, p.care, p.added_at, ca.category_id, ca.name, ca.sex
         | FROM product p
         | INNER JOIN category ca ON p.c_category_id = ca.category_id
         | WHERE p.added_at BETWEEN $startDate AND $endDate""".stripMargin
      .query[StoreProductDB]
      .to[List]

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

  private def getWhereClause(name: Option[String],
                             age: Option[Int],
                             isFavourite: Option[Boolean],
                             addedAfter: Option[LocalDateTime],
                             addedBefore: Option[LocalDateTime],
                             categories: List[CategoryID]): String = {
    val nameFilter = name.map(n => s"UPPER(p.name) LIKE UPPER('$n%')")
    val ageFilter = age.flatMap(count => {
      val currentDate = LocalDate.now().atStartOfDay()
      val referenceTime = currentDate.minusMonths(count.toLong)
      if (addedAfter.isDefined && addedAfter.get.isAfter(referenceTime)) {
        None
      } else {
        val formattedDate =
          referenceTime.format(
            DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS"))
        Some(s"p.added_at >= '$formattedDate'")
      }
    })
    val favouriteFilter =
      isFavourite.map(isFavourite => s"p.is_favourite=$isFavourite")
    val addedAfterFilter = addedAfter.map(time => {
      val formattedTime =
        time.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS"))
      s"p.added_at > '$formattedTime'"
    })
    val addedBeforeFilter = addedBefore.map(time => {
      val formattedTime =
        time.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS"))
      s"p.added_at < '$formattedTime'"
    })
    val categoriesFilter = categories match {
      case Nil => None
      case c =>
        Some(
          s"ca.category_id IN ${c.map(v => s"'$v'").mkString_("(", ",", ")")}")
    }

    val filterList = List(nameFilter,
                          ageFilter,
                          favouriteFilter,
                          addedAfterFilter,
                          addedBeforeFilter,
                          categoriesFilter)
    filterList.filter(_.isDefined).flatten match {
      case Nil                => ""
      case l if l.length == 1 => s"WHERE ${l.head}"
      case l if l.length >= 2 => s"WHERE ${l.mkString_("", " AND ", "")}"
    }
  }
}
