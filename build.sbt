
organizationName := "Opalia Systems"

organizationHomepage := Some(url("https://opalia.systems"))

organization := "systems.opalia"

name := "sbt-opalia"

description := "An sbt plugin for building OSGi bundles and supporting different test modes used in Opalia stack."

homepage := Some(url("https://github.com/OpaliaSystems/sbt-opalia"))

version := "0.1.0"

sbtPlugin := true

addSbtPlugin("com.typesafe.sbt" % "sbt-osgi" % "0.9.5")

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")
