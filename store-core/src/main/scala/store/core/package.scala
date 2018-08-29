package store

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
package object core {

  object Link extends PhantomType[String]
  type Link = Link.Type

  object PageOffset extends PhantomType[Int]
  type PageOffset = PageOffset.Type

  object PageLimit extends PhantomType[Int]
  type PageLimit = PageLimit.Type

}
