package com.github.romer533.utils

import cats.effect.{Async, Resource, Sync}
import cats.syntax.functor._
import com.github.romer533.services.models.{Client, ExchangeResults, Order}
import fs2.io.file.{Files, Path}
import fs2.{text, Stream}

import scala.io.Source

object FileUtils {
  def loadClients[F[_]: Sync](filePath: String): F[Map[String, Client]] =
    fileStream(filePath)
      .map { line =>
        val parts = line.split(" ").filter(_.nonEmpty)
        Client(
          name = parts(0),
          balance = parts(1).toInt,
          stocks = Map(
            "A" -> parts(2).toInt,
            "B" -> parts(3).toInt,
            "C" -> parts(4).toInt,
            "D" -> parts(5).toInt
          )
        )
      }
      .compile
      .toList
      .map(_.map(client => client.name -> client).toMap)

  def loadOrders[F[_]: Sync](filePath: String): F[List[Order]] =
    fileStream(filePath)
      .map { line =>
        val parts = line.split(" ").filter(_.nonEmpty)
        Order(
          clientName = parts(0),
          operation = parts(1),
          stock = parts(2),
          quantity = parts(3).toInt,
          price = parts(4).toInt
        )
      }
      .compile
      .toList

  private def fileStream[F[_]: Sync](filePath: String): Stream[F, String] =
    Stream
      .resource(Resource.fromAutoCloseable(Sync[F].delay(Source.fromFile(filePath))))
      .flatMap(source => Stream.fromIterator[F](source.getLines, 1024))

  def saveResults[F[_]: Async](filePath: String, results: ExchangeResults): F[Unit] = {
    val lines = results.clients.values.map(client =>
      s"${client.name}\t${client.balance}\t${client.stocks("A")}\t${client.stocks("B")}\t${client.stocks("C")}\t${client
        .stocks("D")}"
    )
    Stream
      .emits(lines.toSeq)
      .intersperse("\n")
      .through(text.utf8.encode)
      .through(Files[F].writeAll(Path(filePath)))
      .compile
      .drain
  }
}
