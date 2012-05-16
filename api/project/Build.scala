import sbt._
import Keys._
object MashioApiBuild extends Build {
  val playJson = RootProject(uri("git://github.com/tinystatemachine/play-json.git"))
  lazy val root = Project(id = "mashio-proxy", base = file(".")) dependsOn (playJson)
}