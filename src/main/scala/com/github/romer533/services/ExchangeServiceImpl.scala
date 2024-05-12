package com.github.romer533.services

import com.github.romer533.services.models.{Client, ExchangeResults, Order}

import scala.collection.mutable

class ExchangeServiceImpl extends ExchangeService {
  private val buyOrders  = mutable.Map[String, mutable.PriorityQueue[Order]]()
  private val sellOrders = mutable.Map[String, mutable.PriorityQueue[Order]]()

  private def processBuyOrder(order: Order, clients: Map[String, Client]): Map[String, Client] = {
    var updatedBalances = clients
    val sells =
      sellOrders.getOrElseUpdate(order.stock, mutable.PriorityQueue[Order]()(Ordering.by[Order, Int](_.price).reverse))
    var remainingQuantity = order.quantity
    while (sells.nonEmpty && remainingQuantity > 0 && sells.head.price <= order.price) {
      val sellOrder          = sells.dequeue()
      val quantityToExchange = Math.min(remainingQuantity, sellOrder.quantity)
      updatedBalances = updateBalances(order, sellOrder, quantityToExchange, clients)
      remainingQuantity -= quantityToExchange
      if (sellOrder.quantity > quantityToExchange) {
        sells.enqueue(sellOrder.copy(quantity = sellOrder.quantity - quantityToExchange))
      }
    }
    if (remainingQuantity > 0) {
      buyOrders
        .getOrElseUpdate(order.stock, mutable.PriorityQueue[Order]()(Ordering.by(-_.price)))
        .enqueue(order.copy(quantity = remainingQuantity))
    }
    updatedBalances
  }

  private def processSellOrder(order: Order, clients: Map[String, Client]): Map[String, Client] = {
    var updatedBalances = clients
    val buys = buyOrders.getOrElseUpdate(order.stock, mutable.PriorityQueue[Order]()(Ordering.by[Order, Int](-_.price)))
    var remainingQuantity = order.quantity
    while (buys.nonEmpty && remainingQuantity > 0 && buys.head.price >= order.price) {
      val buyOrder           = buys.dequeue()
      val quantityToExchange = Math.min(remainingQuantity, buyOrder.quantity)
      updatedBalances = updateBalances(buyOrder, order, quantityToExchange, clients)
      remainingQuantity -= quantityToExchange
      if (buyOrder.quantity > quantityToExchange) {
        buys.enqueue(buyOrder.copy(quantity = buyOrder.quantity - quantityToExchange))
      }
    }
    if (remainingQuantity > 0) {
      sellOrders
        .getOrElseUpdate(order.stock, mutable.PriorityQueue[Order]()(Ordering.by[Order, Int](_.price).reverse))
        .enqueue(order.copy(quantity = remainingQuantity))
    }
    updatedBalances
  }

  private def updateBalances(
    buyOrder: Order,
    sellOrder: Order,
    quantity: Int,
    clients: Map[String, Client]
  ): Map[String, Client] = {
    val totalCost = quantity * sellOrder.price
    val buyer     = clients(buyOrder.clientName)
    val seller    = clients(sellOrder.clientName)

    val updatedBuyerStocks = buyer.stocks.updated(buyOrder.stock, buyer.stocks.getOrElse(buyOrder.stock, 0) + quantity)
    val updatedBuyer       = buyer.copy(balance = buyer.balance - totalCost, stocks = updatedBuyerStocks)

    val updatedSellerStocks =
      seller.stocks.updated(sellOrder.stock, seller.stocks.getOrElse(sellOrder.stock, 0) - quantity)
    val updatedSeller = seller.copy(balance = seller.balance + totalCost, stocks = updatedSellerStocks)
    clients.updated(buyOrder.clientName, updatedBuyer).updated(sellOrder.clientName, updatedSeller)
  }

  override def processOrders(clients: Map[String, Client], orders: Seq[Order]): ExchangeResults = {
    var result = clients
    orders.foreach { order =>
      clients.get(order.clientName).foreach { client =>
        if (order.operation.charAt(0) == 'b' && client.balance >= order.quantity * order.price) {
          result = processBuyOrder(order, clients)
        } else if (order.operation.charAt(0) == 's' && client.stocks.getOrElse(order.stock, 0) >= order.quantity) {
          result = processSellOrder(order, clients)
        }
      }
    }
    ExchangeResults(result)
  }
}

object ExchangeServiceImpl {
  def apply(): ExchangeServiceImpl = new ExchangeServiceImpl()
}
