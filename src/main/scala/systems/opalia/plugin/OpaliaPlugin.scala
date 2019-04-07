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
        Resolver.typesafeRepo("maven-releases"),
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots")
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

    val bundle = inputKey[Unit]("Create and deploy bundles.")

    def bundleSettings: Seq[Setting[_]] = {

      val javaVersion = System.getProperty("java.specification.version")

      SbtOsgi.defaultOsgiSettings ++ osgiSettings ++ Seq(

        autoScalaLibrary := false, // exclude scala-library from dependencies

        OsgiKeys.failOnUndecidedPackage := true,

        OsgiKeys.bundleSymbolicName := s"${organization.value}.${name.value.toLowerCase.replace('-', '_')}",

        OsgiKeys.bundleVersion := version.value,

        OsgiKeys.requireCapability := s"""osgi.ee;filter:="(&(osgi.ee=JavaSE)(version=$javaVersion))"""",

        OsgiKeys.privatePackage := Seq(),

        OsgiKeys.importPackage := Seq("org.osgi.framework"),

        OsgiKeys.exportPackage := Seq(),

        OsgiKeys.embeddedJars := dependencyClasspath.in(Runtime).value.map(_.data).filter(_.isFile),

        libraryDependencies ++= Seq(
          "org.osgi" % "org.osgi.core" % "6.0.0" % "provided"
        ),

        autoImport.bundle := {

          val bundleBase = complete.DefaultParsers.fileParser(new File(System.getProperty("user.dir"))).parsed
          val source = OsgiKeys.bundle.value.toPath.toFile
          val target = bundleBase / source.getName

          IO.copyFile(source, target)
        }
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