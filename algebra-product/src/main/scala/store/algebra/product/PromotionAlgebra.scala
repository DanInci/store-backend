package store.algebra.product

import doobie.util.transactor.Transactor
import store.algebra.content.ContentStorageAlgebra
import store.algebra.product.entity._
import store.db.DatabaseContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 12/09/2018
  */
trait PromotionAlgebra[F[_]] {

  def createPromotion(definition: PromotionDefinition): F[PromotionID]

  def getPromotionById(promotionId: PromotionID): F[Promotion]

  def getAllPromotions: F[List[Promotion]]

  def getActivePromotions: F[List[Promotion]]

  def deletePromotion(promotionId: PromotionID): F[Unit]

}

object PromotionAlgebra {
  import store.effects._

  def async[F[_]: Async](contentAlgebra: ContentStorageAlgebra[F])(implicit transactor: Transactor[F], dbContext: DatabaseContext[F]): PromotionAlgebra[F] = new impl.AsyncAlgebraImpl[F](contentAlgebra)
}
