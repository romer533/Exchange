package com.github.romer533.services.models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema
import sttp.tapir.Schema.derived

final case class ExchangeResults(clients: Map[String, Client])

object ExchangeResults {
  implicit val schema: Schema[ExchangeResults] = derived[ExchangeResults]
  implicit val codec: Codec[ExchangeResults]   = deriveCodec[ExchangeResults]
}
