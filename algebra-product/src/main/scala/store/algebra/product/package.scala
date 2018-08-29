package store.algebra

import store.core.PhantomType

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
package object product {

  object ProductID extends PhantomType[Long]
  type ProductID = ProductID.Type

  object CategoryID extends PhantomType[Int]
  type CategoryID = CategoryID.Type

  object Count extends PhantomType[Int]
  type Count = Count.Type

  object Price extends PhantomType[Double]
  type Price = Price.Type

  object DescParagraph extends PhantomType[String]
  type DescParagraph = DescParagraph.Type

  object CareParagraph extends PhantomType[String]
  type CareParagraph = CareParagraph.Type

  type ModuleProductAlgebra[F[_]] = ProductAlgebra[F] with ProductStockAlgebra[F]
}
