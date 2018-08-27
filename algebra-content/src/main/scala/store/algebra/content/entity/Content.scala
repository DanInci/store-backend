package store.algebra.content.entity

import store.algebra.content._
import store.core.Link

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 27/08/2018
  */

trait Content extends Serializable {
  def contentID: ContentID
  def name: String
  def content: BinaryContent
  def format: Format
}

trait ContentLink extends Serializable {
  def name: String
  def link: Link
  def format: Format
}

trait ContentDefinition extends Serializable {
  def name: String
  def content: BinaryContent
  def format: Format
}
