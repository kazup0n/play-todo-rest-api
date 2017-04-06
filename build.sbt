name := """todo-rest-api"""
organization := "com.github.kazup0n"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.7.19" % Test
libraryDependencies += "org.flywaydb" % "flyway-play_2.11" % "3.0.1"
libraryDependencies += "com.h2database" % "h2" % "1.4.193"
libraryDependencies += "org.scalikejdbc" % "scalikejdbc_2.11" % "2.5.1"
libraryDependencies += "org.scalikejdbc" % "scalikejdbc-config_2.11" % "2.5.1"
libraryDependencies += "org.scalikejdbc" % "scalikejdbc-play-initializer_2.11" % "2.5.1"
libraryDependencies += "org.skinny-framework" % "skinny-orm_2.11" % "2.3.6"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.github.kazup0n.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.github.kazup0n.binders._"
