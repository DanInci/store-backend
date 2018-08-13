package store.algebra.product

import doobie.util.transactor.Transactor
import store.algebra.content.ContentStorageAlgebra
import store.algebra.product.entity.Stock
import store.core.ProductID
import store.db.DatabaseContext

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
trait ProductStockAlgebra[F[_]] {

  def getStock(productId: ProductID): F[List[Stock]]

  def addStock(stock: Stock, productId: ProductID): F[Unit]

  def removeStock(stock: Stock, productId: ProductID): F[Unit]

}

object ProductStockAlgebra {
  import store.effects._

  def async[F[_]: Async](implicit transactor: Transactor[F], dbContext: DatabaseContext[F], contentAlgebra: ContentStorageAlgebra[F]): ProductAlgebra[F] = new impl.AsyncAlgebraImpl[F]()
}
