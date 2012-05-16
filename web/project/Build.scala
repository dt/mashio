import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "mashio"
    val appVersion      = "1.0-SNAPSHOT"

    val core = uri("../core")

    val main = PlayProject(appName, appVersion, mainLang = SCALA).settings(
        templatesImport ++= Seq(
            "net.reisub.mashio.models._",
            "net.reisub.mashio.models.BoxedId._"),
        routesImport ++= Seq(
            "net.reisub.mashio.models._",
            "net.reisub.mashio.models.BoxedId._")
    ).dependsOn(core)
}
