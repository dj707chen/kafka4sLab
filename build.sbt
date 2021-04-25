import sbt.url

organization := "lab"
name := "kafka4sLab"
version := "0.0.1"

homepage := Option(url("https://github.com/dj707chen/kafka4sLab"))
licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
description := """Try kafka4s"""
developers := List(
    Developer(
        id    = "dj707chen",
        name  = "DJ Chen",
        email = "@dj707chen",
        url   = url("https://github.com/dj707chen")
    )
)

scalaVersion := "2.13.5"
val kafka4s = "com.banno" %% "kafka4s" % "3.0.0-M25"

val pprint = "com.lihaoyi" %% "pprint" % "0.5.6"

val scalaTest = "org.scalatest" %% "scalatest" % "3.1.4" % Test

libraryDependencies ++= Seq(
    pprint,
    kafka4s,
    scalaTest)

parallelExecution := false

// confluent library not published to Maven central
resolvers += "confluent" at "https://packages.confluent.io/maven/"

