import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "mashio"
    val appVersion      = "1.0-SNAPSHOT"

	val novusRels = "repo.novus rels" at "http://repo.novus.com/releases/"
	val novusSnaps = "repo.novus snaps" at "http://repo.novus.com/snapshots/"

    val appDependencies = Seq(
        "se.radley" %% "play-plugins-salat" % "1.0.3",
        "joda-time" % "joda-time" % "2.1",
        "org.mongodb" % "bson" % "2.7.3",
        "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT",
        "org" % "jaudiotagger" % "2.0.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
        resolvers += novusRels,
        templatesImport ++= Seq(
            "net.reisub.mashio.models._",
            "net.reisub.mashio.models.BoxedId._"),
        routesImport ++= Seq(
            "net.reisub.mashio.models._",
            "net.reisub.mashio.models.BoxedId._")
    )
}
