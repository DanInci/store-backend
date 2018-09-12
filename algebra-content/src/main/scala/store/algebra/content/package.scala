package store.algebra

import store.core.PhantomType

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 05/08/2018
  */
package object content {

  object ContentID extends PhantomType[String]
  type ContentID = ContentID.Type

  object BucketName extends PhantomType[String]
  type BucketName = BucketName.Type

  object Path extends PhantomType[String]
  type Path = Path.Type

  object BinaryContent extends PhantomType[Array[Byte]]
  type BinaryContent = BinaryContent.Type

}
