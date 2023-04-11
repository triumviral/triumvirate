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
  .enablePlugins(
    AkkaGrpcPlugin,
  )
  .settings(
    scalaVersion := dottyLatestNightlyBuild.get,
    name := "triumvirate",
    libraryDependencies ++= Seq.concat(
      Seq(
        "akka-actor-typed",
        "akka-stream-typed",
        "akka-persistence-typed",
      ).map("com.typesafe.akka" %% _ % "2.7.0").map(_.cross(CrossVersion.for3Use2_13)),
      Seq(
        "akka-http",
      ).map("com.typesafe.akka" %% _ % "10.4.0").map(_.cross(CrossVersion.for3Use2_13)),
      Seq(
        "org.redisson" % "redisson" % "3.19.1",
        "ch.qos.logback" % "logback-classic" % "1.4.5",
        "com.typesafe" % "config" % "1.4.2",
        "net.logstash.logback" % "logstash-logback-encoder" % "7.2",
        "org.codehaus.janino" % "janino" % "3.1.9",
        "org.fusesource.jansi" % "jansi" % "2.4.0",
      ),
      Seq(
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.1",
        "com.lightbend.akka.grpc" %% "akka-grpc-runtime" % "2.2.1",
        "org.sangria-graphql" %% "sangria" % "3.5.0",
        "org.sangria-graphql" %% "sangria-relay" % "3.0.0",
      ).map(_.cross(CrossVersion.for3Use2_13)),
    ),
    excludeDependencies ++= Seq.concat(
      Seq(
        "com.thesamet.scalapb" %% "scalapb-runtime",
      ),
    ),
  )
