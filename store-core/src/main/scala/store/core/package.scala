package store

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
package object core {

  object Title extends PhantomType[String]
  type Title = Title.Type

  object Description extends PhantomType[String]
  type Description = Description.Type

  object PageOffset extends PhantomType[Int]
  type PageOffset = PageOffset.Type

  object PageLimit extends PhantomType[Int]
  type PageLimit = PageLimit.Type

}
