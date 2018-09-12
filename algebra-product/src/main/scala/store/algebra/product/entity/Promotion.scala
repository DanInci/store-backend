package store.algebra.product.entity

import store.algebra.content.entity.Content
import store.algebra.product._
import store.algebra.product.db.ProductSql.PromotionDB
import store.core._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 11/09/2018
  */
final case class Promotion(
    promotionId: PromotionID,
    title: Title,
    description: Description,
    promotedProductId: Option[ProductID],
    image: Content,
    expiresAt: PromotionExpiration
) extends Serializable

object Promotion {

  def fromPromotionDB(promotionDB: PromotionDB, image: Content): Promotion =
    Promotion(
      promotionDB.promotionId,
      promotionDB.title,
      promotionDB.description,
      promotionDB.promotedProductId,
      image,
      promotionDB.expiresAt
    )

}

final case class PromotionDefinition(
    title: Title,
    description: Description,
    promotedProductId: Option[ProductID],
    image: Content,
    expiresAt: PromotionExpiration
)
