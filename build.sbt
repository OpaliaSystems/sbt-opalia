
organizationName := "Opalia Systems"

organization := "systems.opalia"

name := "sbt-opalia"

homepage := Some(url("https://github.com/OpaliaSystems/sbt-opalia"))

version := "0.1.0"

sbtPlugin := true

addSbtPlugin("com.typesafe.sbt" % "sbt-osgi" % "0.9.5")

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")
