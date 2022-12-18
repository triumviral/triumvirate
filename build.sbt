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
      ).map("com.typesafe.akka" %% _ % "2.8.0-M1"),
      Seq(
        "akka-http",
      ).map("com.typesafe.akka" %% _ % "10.5.0-M1"),
      Seq(
      ),
    ),
  )
