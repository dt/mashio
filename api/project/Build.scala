import sbt._
import Keys._
object MashioApiBuild extends Build {
  val playJson = uri("git://github.com/tinystatemachine/play-json.git")
  val core = uri("../core")
  lazy val root = Project(id = "api", base = file(".")) dependsOn (playJson, core)
}