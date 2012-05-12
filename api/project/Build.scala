import sbt._
import Keys._

object MashioApiBuild extends Build {

  val buildOrganization = "net.reisub"
  val buildVersion      = "0.1"
  val buildScalaVersion = "2.9.1"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )

  val res = Seq(
    "repo.codahale.com" at "http://repo.codahale.com",
    "twitter.com" at "http://maven.twttr.com/"
  )

  val deps = Seq(
    "com.twitter"           % "finagle-core_2.9.1"  % "1.9.12",
    "com.twitter"           % "finagle-http_2.9.1"  % "1.9.12",
    "org.codehaus.jackson"  % "jackson-core-asl"    % "1.9.6",
    "org.codehaus.jackson"  % "jackson-mapper-asl"  % "1.9.6",
    "com.codahale"          % "jerkson_2.9.1"       % "0.4.2"
  )

  val playJson = RootProject(uri("git://github.com/tinystatemachine/play-json.git"))

  lazy val root = Project(id = "mashio-proxy",
                            base = file("."),
                            settings = buildSettings ++ Seq(
                              resolvers ++= res,
                              libraryDependencies ++= deps
                            )
  ) dependsOn (playJson)
}