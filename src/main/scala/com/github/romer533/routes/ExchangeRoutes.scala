package com.github.romer533.routes

import cats.effect.{Async, Sync}
import com.github.romer533.services.ExchangeService
import com.github.romer533.services.models.{Client, ExchangeInput, ExchangeResults, Order}
import org.http4s.HttpApp
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter

class ExchangeRoutes[F[_]: Async](exchangeService: ExchangeService) {
  def routes: HttpApp[F] = Http4sServerInterpreter().toRoutes(executeByJsonEndpoint).orNotFound

  private def executeByJsonRoute(clients: Map[String, Client], orders: Seq[Order]): F[ExchangeResults] =
    Sync[F].delay(exchangeService.processOrders(clients, orders))

  private def executeByJsonEndpoint: ServerEndpoint[Any, F] =
    endpoint.get
      .in("exchange" / "execute")
      .in(jsonBody[ExchangeInput])
      .out(jsonBody[ExchangeResults])
      .serverLogicSuccess(input =>
        executeByJsonRoute(input.clients.map(client => client.name -> client).toMap, input.orders)
      )
}

object ExchangeRoutes {
  def apply[F[_]: Async](exchangeService: ExchangeService): ExchangeRoutes[F] =
    new ExchangeRoutes(exchangeService)
}
