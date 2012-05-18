resolvers += "repo.novus snaps" at "http://repo.novus.com/snapshots/"

libraryDependencies ++= Seq(
  "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT",
  "org.slf4j" % "slf4j-jdk14" % "1.6.4"
)