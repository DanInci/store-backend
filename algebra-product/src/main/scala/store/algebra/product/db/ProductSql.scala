package store.algebra.product.db

import store.core._
import doobie._
import doobie.implicits._
import cats.implicits._
import store.algebra.product._
import store.algebra.product.db.entity._
import store.algebra.product.entity._
import store.algebra.product.entity.component.ProductSize
import store.core.entity.Category

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
object ProductSql extends ProductComposites {

  // CATEGORY QUERIES

  def findAllCategories: ConnectionIO[List[Category]] = {
    sql"SELECT category_id, name FROM category".query[Category].to[List]
  }

  // PRODUCT QUERIES

  def insertProduct(
      definition: StoreProductDefinition): ConnectionIO[ProductID] = {
    sql"""INSERT INTO product (c_category_id, name, price, discount, availability_on_command, description, care)
         | VALUES (${definition.categoryId},${definition.name},${definition.price},${definition.discount},${definition.isAvailableOnCommand},${definition.description}, ${definition.care})""".stripMargin.update
      .withUniqueGeneratedKeys("product_id")
  }

  def findById(productId: ProductID): ConnectionIO[Option[StoreProductDB]] =
    sql"""SELECT p.product_id, p.name, p.price, p.discount, p.availability_on_command, p.description, p.care, c.category_id, c.name
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

    (sql"""SELECT p.product_id, p.name, p.price, p.discount, p.availability_on_command, p.description, p.care, c.category_id, c.name
         | FROM product p
         | INNER JOIN category c ON p.c_category_id = c.category_id """.stripMargin
      ++ Fragment.const(whereClause) ++
      sql" LIMIT $limit OFFSET ${offset*limit}")
      .query[StoreProductDB]
      .to[List]

  }

  def deleteProduct(productId: ProductID): ConnectionIO[Int] =
    sql"DELETE FROM product WHERE product_id=$productId".update.run

  // STOCKS QUERIES

  def addStockToProduct(stock: Stock, productId: ProductID): ConnectionIO[Int] =
    sql"INSERT INTO stock (p_product_id, size, count) VALUES ($productId, ${stock.size}, ${stock.count})".update.run

  def findStocksByProductID(productId: ProductID): ConnectionIO[List[Stock]] =
    sql"SELECT size, count FROM stock WHERE p_product_id=$productId"
      .query[Stock]
      .to[List]

  def findStockByProductIdAndSize(
      productId: ProductID,
      productSize: ProductSize): ConnectionIO[Option[Stock]] = {
    sql"SELECT size, count FROM stock WHERE p_product_id=$productId AND size=$productSize"
      .query[Stock]
      .option
  }

  def updateStockByProductID(count: Count,
                             productId: ProductID): ConnectionIO[Int] =
    sql"UPDATE stock SET count=$count WHERE p_product_id=$productId".update.run

  def updateStockByProductIDAndSize(count: Count,
                                    productId: ProductID,
                                    size: ProductSize): ConnectionIO[Int] =
    sql"UPDATE stock SET count=$count WHERE p_product_id=$productId AND size=$size".update.run

  def deleteStockByProductID(productId: ProductID): ConnectionIO[Int] =
    sql"DELETE FROM stock WHERE p_product_id=$productId".update.run

  def deleteStockByProductIDAndSize(productId: ProductID,
                                    size: ProductSize): ConnectionIO[Int] =
    sql"DELETE FROM stock WHERE p_product_id=$productId AND size=$size".update.run

}
