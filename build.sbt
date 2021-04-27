name := """justbuy"""
organization := "com.shiftio"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.4"
val circeVersion = "0.12.3"

resolvers += Resolver.mavenLocal
resolvers += Resolver.bintrayRepo("mattmoore", "bcrypt-scala")

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.15"
libraryDependencies += "io.mattmoore" %% "bcrypt-scala" % "0.1.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

fork in run := true


