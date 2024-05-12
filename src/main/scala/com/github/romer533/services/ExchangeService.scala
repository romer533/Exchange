package com.github.romer533.services

import com.github.romer533.services.models.{Client, ExchangeResults, Order}

trait ExchangeService {
  def processOrders(clients: Map[String, Client], orders: Seq[Order]): ExchangeResults
}
