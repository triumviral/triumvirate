fork := true
javaOptions ++= Seq(
  "-Dconfig.resource=configuration.conf",
)
lazy val root = project
  .in(file("."))
  .settings(
    scalaVersion := dottyLatestNightlyBuild.get,
    name := "triumvirate",
    libraryDependencies ++= Seq.concat(
      Seq(
        "akka-actor-typed",
        "akka-stream-typed",
        "akka-persistence-typed",
      ).map("com.typesafe.akka" %% _ % "2.8.0-M2"),
      Seq(
        "akka-http",
      ).map("com.typesafe.akka" %% _ % "10.5.0-M1"),
      Seq(
        "org.redisson" % "redisson" % "3.19.0",
        "ch.qos.logback" % "logback-classic" % "1.4.5",
        "com.typesafe" % "config" % "1.4.2",
      ),
    ),
  )
