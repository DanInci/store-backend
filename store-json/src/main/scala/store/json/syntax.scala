package store.json

import busymachines.json.{JsonSyntax, SemiAutoDerivation}

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 13/08/2018
  */
object syntax extends JsonSyntax.Implicits

object derive extends SemiAutoDerivation

object autoderive extends io.circe.generic.extras.AutoDerivation
