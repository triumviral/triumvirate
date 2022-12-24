fork := true
javaOptions ++= Seq(
  "-Dconfig.resource=cfg/application.conf",
  "-Dcfg=cfg/configuration.conf",
)
scalacOptions ++= Seq(
  "-feature",
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
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.1",
        "net.logstash.logback" % "logstash-logback-encoder" % "7.2",
      ),
    ),
  )
