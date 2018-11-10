package store.algebra.content.entity

import java.util.Base64

import store.algebra.content._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 27/08/2018
  */
final case class Content(
    name: String,
    content: String,
    isBase64Encoded: Boolean,
    format: Format
) extends Serializable {

  def getContent: Either[ContentID, BinaryContent] =
    if (isBase64Encoded) Right(BinaryContent(Base64.getDecoder.decode(content)))
    else Left(ContentID(content))

}

object Content {

  def fromBinary(name: String, binary: BinaryContent, format: Format): Content =
    Content(name,
            Base64.getEncoder.encodeToString(binary),
            isBase64Encoded = true,
            format)

  def fromContentID(name: String,
                    contentId: ContentID,
                    format: Format): Content =
    Content(name, contentId, isBase64Encoded = false, format)

}
