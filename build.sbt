name := """marvel-heroes"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  guice,
  ws,
  "io.lettuce" % "lettuce-core" % "5.1.3.RELEASE",
  "org.mongodb" % "mongodb-driver-reactivestreams" % "1.13.1"
)
