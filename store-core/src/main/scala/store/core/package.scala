package store

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
package object core {

  object ProductID extends PhantomType[Long]
  type ProductID = ProductID.Type

}
