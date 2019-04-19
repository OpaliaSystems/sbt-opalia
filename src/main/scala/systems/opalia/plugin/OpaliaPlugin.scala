package systems.opalia.plugin

import com.typesafe.sbt.MultiJvmPlugin
import com.typesafe.sbt.MultiJvmPlugin.autoImport._
import com.typesafe.sbt.osgi.SbtOsgi
import com.typesafe.sbt.osgi.SbtOsgi.autoImport._
import sbt.Keys._
import sbt._


object OpaliaPlugin
  extends AutoPlugin {

  private val IntegrationTest = config("it") extend Test
  private val SystemTest = config("sys") extend Test
  private val EndToEndTest = config("e2e") extend Test
  private val ManualTest = config("man") extend Test

  private val settingsDefault =
    Seq(

      resolvers ++= Seq(
        Resolver.mavenLocal,
        Resolver.jcenterRepo,
        Resolver.bintrayRepo("typesafe", "maven-releases"),
        Opts.resolver.sonatypeReleases,
        Opts.resolver.sonatypeSnapshots
      ),

      scalacOptions ++= Seq(
        "-unchecked",
        "-deprecation",
        "-feature"
      )
    )

  private val settingsMultiJvmTest =
    inConfig(MultiJvm)(MultiJvmPlugin.multiJvmSettings) ++
      Seq(

        resourceDirectory in MultiJvm := baseDirectory.value / "src/test-multi-jvm/resources",
        javaSource in MultiJvm := baseDirectory.value / "src/test-multi-jvm/java",
        scalaSource in MultiJvm := baseDirectory.value / "src/test-multi-jvm/scala",
      )

  private val settingsIntegrationTest =
    inConfig(IntegrationTest)(Defaults.testSettings) ++
      Seq(

        resourceDirectory in IntegrationTest := baseDirectory.value / "src/test-it/resources",
        javaSource in IntegrationTest := baseDirectory.value / "src/test-it/java",
        scalaSource in IntegrationTest := baseDirectory.value / "src/test-it/scala",
      )

  private val settingsSystemTest =
    inConfig(SystemTest)(Defaults.testSettings) ++
      Seq(

        resourceDirectory in SystemTest := baseDirectory.value / "src/test-sys/resources",
        javaSource in SystemTest := baseDirectory.value / "src/test-sys/java",
        scalaSource in SystemTest := baseDirectory.value / "src/test-sys/scala"
      )

  private val settingsEndToEndTest =
    inConfig(EndToEndTest)(Defaults.testSettings) ++
      Seq(

        resourceDirectory in EndToEndTest := baseDirectory.value / "src/test-e2e/resources",
        javaSource in EndToEndTest := baseDirectory.value / "src/test-e2e/java",
        scalaSource in EndToEndTest := baseDirectory.value / "src/test-e2e/scala"
      )

  private val settingsManualTest =
    inConfig(ManualTest)(Defaults.testSettings) ++
      Seq(

        resourceDirectory in ManualTest := baseDirectory.value / "src/test-man/resources",
        javaSource in ManualTest := baseDirectory.value / "src/test-man/java",
        scalaSource in ManualTest := baseDirectory.value / "src/test-man/scala"
      )

  object autoImport {

    def bundleSettings: Seq[Setting[_]] = {

      SbtOsgi.defaultOsgiSettings ++ osgiSettings ++ Seq(

        exportJars := true,

        autoScalaLibrary := false, // exclude scala-library from dependencies

        OsgiKeys.failOnUndecidedPackage := true,

        OsgiKeys.bundleSymbolicName := s"${organization.value}.${name.value.toLowerCase.replace('-', '_')}",

        OsgiKeys.bundleVersion := version.value,

        OsgiKeys.privatePackage := Seq(),

        OsgiKeys.importPackage := Seq("org.osgi.framework.*"),

        OsgiKeys.exportPackage := Seq(),

        OsgiKeys.embeddedJars := dependencyClasspath.in(Runtime).value.map(_.data).filter(_.isFile)
      )
    }
  }

  override def requires: Plugins =
    plugins.JvmPlugin

  override def trigger: PluginTrigger =
    allRequirements

  override def projectConfigurations: Seq[Configuration] =
    Seq(
      MultiJvm,
      IntegrationTest,
      SystemTest,
      EndToEndTest,
      ManualTest
    )

  override def projectSettings: Seq[Def.Setting[_]] =
    settingsDefault ++
      settingsMultiJvmTest ++
      settingsIntegrationTest ++
      settingsSystemTest ++
      settingsEndToEndTest ++
      settingsManualTest
}
