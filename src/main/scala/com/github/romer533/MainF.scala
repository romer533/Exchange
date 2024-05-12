package com.github.romer533

import cats.effect.{Async, Resource, Sync}
import cats.syntax.flatMap._
import com.github.romer533.configs.Configs
import com.github.romer533.routes.ExchangeRoutes
import com.github.romer533.servers.Http4sServer
import com.github.romer533.services.ExchangeServiceImpl
import com.github.romer533.services.models.ExchangeResults
import com.github.romer533.utils.FileUtils
import org.typelevel.log4cats.Logger

trait MainF {
  def runProgram[F[_]: Async: Logger]: F[Unit] = {
    val definition: Resource[F, Unit] =
      for {
        configs <- Configs.getFullConfig[F]
        clients <- Resource.eval(FileUtils.loadClients("src/main/resources/clients.txt"))
        orders  <- Resource.eval(FileUtils.loadOrders("src/main/resources/orders.txt"))
        service = ExchangeServiceImpl()
        result  = service.processOrders(clients, orders).clients
        routes  = ExchangeRoutes(service)
        _ <- Resource.eval(FileUtils.saveResults("src/main/resources/results.txt", ExchangeResults(result)))
        _ <- Http4sServer(configs).run(routes.routes)
      } yield ()

    Sync[F].handleErrorWith(definition.use(_ => Sync[F].never[Unit])) { e =>
      Logger[F].error(s"Any error $e").flatTap(_ => Sync[F].delay(e.printStackTrace()))
    }
  }
}
