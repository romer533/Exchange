package com.github.romer533.configs

import cats.effect.{Resource, Sync}
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigSource}

import scala.util.Try

final case class Configs(server: ServerConfig)

object Configs {
  implicit def hint[A]: ProductHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))
  def getFullConfig[F[_]: Sync]: Resource[F, Configs] =
    Resource.eval(Sync[F].fromTry(Try(ConfigSource.default.loadOrThrow[Configs])))
}
