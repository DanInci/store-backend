package store

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
package object core {

  object ProductID extends PhantomType[Long]
  type ProductID = ProductID.Type

  object CategoryID extends PhantomType[Int]
  type CategoryID = CategoryID.Type

  object Link extends PhantomType[String]
  type Link = Link.Type

  object PageOffset extends PhantomType[Int]
  type PageOffset = PageOffset.Type

  object PageLimit extends PhantomType[Int]
  type PageLimit = PageLimit.Type

}
