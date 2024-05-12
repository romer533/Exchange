package com.github.romer533.services.models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema
import sttp.tapir.Schema.derived

final case class Order(clientName: String, operation: String, stock: String, quantity: Int, price: Int)

object Order {
  implicit val schema: Schema[Order] = derived
  implicit val codec: Codec[Order]   = deriveCodec
}
