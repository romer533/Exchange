package com.github.romer533.servers

import cats.effect.{Async, Resource}
import com.github.romer533.configs.Configs
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.{CORS, GZip}

import scala.concurrent.duration.Duration

class Http4sServer[F[_]: Async](cfg: Configs) {

  def run(app: HttpApp[F]): Resource[F, Server] =
    BlazeServerBuilder[F]
      .bindHttp(cfg.server.port, cfg.server.host)
      .withIdleTimeout(Duration.Inf)
      // https://github.com/http4s/http4s/security/advisories/GHSA-52cf-226f-rhr6
      .withHttpApp(GZip(CORS.policy.withAllowOriginAll.withAllowCredentials(false).apply(app)))
      .resource
}

object Http4sServer {
  def apply[F[_]: Async](cfg: Configs): Http4sServer[F] = new Http4sServer[F](cfg)
}
