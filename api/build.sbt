resolvers ++= Seq(
  "repo.codahale.com" at "http://repo.codahale.com",
  "twitter.com" at "http://maven.twttr.com/",
  "repo.novus snaps" at "http://repo.novus.com/snapshots/"
)

libraryDependencies ++= Seq(
  "com.twitter"           % "finagle-core_2.9.1"  % "1.9.12",
  "com.twitter"           % "finagle-http_2.9.1"  % "1.9.12"
)