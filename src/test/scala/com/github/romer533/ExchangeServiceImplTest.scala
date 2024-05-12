package com.github.romer533

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.github.romer533.services.ExchangeServiceImpl
import com.github.romer533.services.models.{Client, ExchangeResults, Order}
import com.github.romer533.utils.FileUtils
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExchangeServiceImplTest extends AnyFlatSpec with Matchers {

  "ExchangeServiceImpl" should "correctly match buy and sell orders" in {
    val service = new ExchangeServiceImpl()
    val clients = Map(
      "C1" -> Client("C1", 1000, Map("A" -> 10)),
      "C2" -> Client("C2", 2000, Map("A" -> 30))
    )

    val orders = Seq(
      Order("C1", "b", "A", 5, 10),
      Order("C2", "s", "A", 5, 10)
    )

    val result = service.processOrders(clients, orders).clients

    result("C1").balance shouldBe 950
    result("C1").stocks("A") shouldBe 15
    result("C2").balance shouldBe 2050
    result("C2").stocks("A") shouldBe 25
  }

  it should "handle partial order fulfillment" in {
    val service = new ExchangeServiceImpl()
    val clients = Map(
      "C1" -> Client("C1", 1000, Map("A" -> 10)),
      "C3" -> Client("C3", 1500, Map("A" -> 20))
    )

    val orders = Seq(
      Order("C1", "b", "A", 15, 10),
      Order("C3", "s", "A", 10, 10)
    )

    val result = service.processOrders(clients, orders).clients

    result("C1").balance shouldBe 900
    result("C1").stocks("A") shouldBe 20
    result("C3").balance shouldBe 1600
    result("C3").stocks("A") shouldBe 10
  }

  it should "reject orders when insufficient funds or stocks" in {
    val service = new ExchangeServiceImpl()
    val clients = Map(
      "C1" -> Client("C1", 50, Map("A" -> 10)),
      "C3" -> Client("C3", 1500, Map("A" -> 20))
    )

    val orders = Seq(
      Order("C1", "b", "A", 10, 10),
      Order("C3", "s", "A", 25, 10)
    )

    val result = service.processOrders(clients, orders).clients

    result("C1").balance shouldBe 50
    result("C1").stocks("A") shouldBe 10
    result("C3").balance shouldBe 1500
    result("C3").stocks("A") shouldBe 20
  }

  it should "process with file" in {
    val service = new ExchangeServiceImpl()
    (for {
      clients <- FileUtils.loadClients[IO]("src/test/resources/clients.txt")
      orders  <- FileUtils.loadOrders[IO]("src/test/resources/orders.txt")
      result = service.processOrders(clients, orders).clients
      _ <- FileUtils.saveResults[IO]("src/test/resources/results.txt", ExchangeResults(result))
    } yield ()).unsafeRunSync()
    true shouldBe true
  }
}
