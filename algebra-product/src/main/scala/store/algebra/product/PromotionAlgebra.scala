package store.algebra.product

import doobie.util.transactor.Transactor
import store.algebra.content._
import store.algebra.content.entity.Content
import store.db.DatabaseContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 19/01/2019
  */
trait PromotionAlgebra[F[_]] {

  def getPromotions: F[List[Content]]

  def createPromotion(content: Content): F[ContentID]

  def removeContent(contentId: ContentID): F[Unit]

}

object PromotionAlgebra {
  import store.effects._

  def async[F[_]: Async](contentAlgebra: ContentStorageAlgebra[F])(implicit transactor: Transactor[F], dbContext: DatabaseContext[F]): PromotionAlgebra[F] = new impl.AsyncAlgebraImpl[F](contentAlgebra)
}
