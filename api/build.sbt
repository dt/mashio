resolvers ++= Seq(
  "repo.codahale.com" at "http://repo.codahale.com",
  "twitter.com" at "http://maven.twttr.com/"
)

libraryDependencies ++= Seq(
  "com.twitter"           % "finagle-core_2.9.1"  % "1.9.12",
  "com.twitter"           % "finagle-http_2.9.1"  % "1.9.12",
  "org.codehaus.jackson"  % "jackson-core-asl"    % "1.9.6",
  "org.codehaus.jackson"  % "jackson-mapper-asl"  % "1.9.6",
  "com.codahale"          % "jerkson_2.9.1"       % "0.4.2"
)