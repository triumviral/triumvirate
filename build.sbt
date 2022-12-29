fork := true
javaOptions ++= Seq(
  "-Dconfig.resource=cfg/application.conf",
  "-Dlogback.configurationFile=log/logback/logback.xml",
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
      ).map("com.typesafe.akka" %% _ % "2.7.0"),
      Seq(
        "akka-http",
      ).map("com.typesafe.akka" %% _ % "10.5.0-M1"),
      Seq(
        "org.redisson" % "redisson" % "3.19.0",
        "ch.qos.logback" % "logback-classic" % "1.4.5",
        "com.typesafe" % "config" % "1.4.2",
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.1",
        "net.logstash.logback" % "logstash-logback-encoder" % "7.2",
        "org.codehaus.janino" % "janino" % "3.1.9",
        "com.lightbend.akka.grpc" %% "akka-grpc-runtime" % "2.3.0-M1",
      ),
    ),
  )
