package com.github.romer533.services.models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema
import sttp.tapir.Schema.derived

final case class ExchangeInput(clients: Seq[Client], orders: Seq[Order])

object ExchangeInput {
  implicit val schema: Schema[ExchangeInput] = derived
  implicit val codec: Codec[ExchangeInput]   = deriveCodec
}
