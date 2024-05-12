ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "Exchange",
    libraryDependencies ++= Seq(
      "org.typelevel"                 %% "cats-core"                     % "2.7.0",
      "org.typelevel"                 %% "cats-effect"                   % "3.3.12",
      "org.typelevel"                 %% "log4cats-core"                 % "2.3.0",
      "org.typelevel"                 %% "log4cats-slf4j"                % "2.3.0",
      "ch.qos.logback"                 % "logback-classic"               % "1.2.11",
      "com.github.pureconfig"         %% "pureconfig"                    % "0.17.1",
      "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"           % "0.19.3",
      "com.softwaremill.sttp.tapir"   %% "tapir-sttp-client"             % "0.19.3",
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"              % "0.19.3",
      "com.softwaremill.sttp.client3" %% "circe"                         % "3.8.7",
      "com.softwaremill.sttp.client3" %% "slf4j-backend"                 % "3.8.7",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-fs2" % "3.8.7",
      "org.slf4j"                      % "slf4j-nop"                     % "1.7.32",
      "org.scalatest"                 %% "scalatest"                     % "3.2.9" % Test
    )
  )
