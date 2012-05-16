resolvers += "repo.novus snaps" at "http://repo.novus.com/snapshots/"

libraryDependencies ++= Seq(
  "se.radley" %% "play-plugins-salat" % "1.0.3",
  "joda-time" % "joda-time" % "2.1",
  "org.mongodb" % "bson" % "2.7.3",
  "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT",
  "org" % "jaudiotagger" % "2.0.3"
)