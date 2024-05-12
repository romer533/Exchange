package com.github.romer533.services.models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema
import sttp.tapir.Schema.derived

final case class Client(name: String, balance: Int, stocks: Map[String, Int])

object Client {
  implicit val schema: Schema[Client] = derived
  implicit val codec: Codec[Client]   = deriveCodec
}
