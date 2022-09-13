ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val akkaVersion = "2.6.19"
val akkaHttpVersion = "10.2.9"
val jwtAkkaHttpVersion = "1.4.4"
val akkaProjectionVersion = "1.2.5"
val http4sVersion = "0.23.9"
val catsCoreVersion = "2.8.0"
val slickVersion = "3.3.3"
val postgresVersion = "42.3.6"
val logbackVersion = "1.2.11"
val scalaTestVersion = "3.2.12"

lazy val commonJavaOptions = Seq(
  "-parameters",
  "-Dio.netty.tryReflectionSetAccessible=true",
  "-Dsun.reflect.debugModuleAccessChecks=access",
  "-Xms128m",
  "-Xmx1024m"
)

lazy val commonScalaOptions = Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-language:_",
  "-Ydelambdafy:method",
  "-target:jvm-1.8",
  "-Yrangepos",
  "-Ywarn-unused"
)

lazy val commonSettings = Seq(
  Compile / javacOptions ++= commonJavaOptions,
  scalacOptions ++= commonScalaOptions,
  run / fork := false,
  Global / cancelable := false,
  libraryDependencies ++= Seq(
    // cats
    "org.typelevel" %% "cats-core" % catsCoreVersion,
    // logging
    "ch.qos.logback" % "logback-classic" % logbackVersion,
  )
)

lazy val `write-api` = (project in file("write-api"))
  .settings(commonSettings)
  .settings(
    name := "counter-write-api",
    libraryDependencies ++= Seq(
      // actor
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      // persistence
      "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
      "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,
      // cassandra plugins
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "1.0.6",
      "software.aws.mcs" % "aws-sigv4-auth-cassandra-java-driver-plugin" % "4.0.6",
      // cluster
      "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      // http
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      // projection
      "com.lightbend.akka" %% "akka-projection-eventsourced" % akkaProjectionVersion,
      "com.lightbend.akka" %% "akka-projection-cassandra" % akkaProjectionVersion,
      // serialization
      "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.4",
      "org.apache.tinkerpop" % "tinkergraph-gremlin" % "3.6.1",
      // logging
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    )
  )

lazy val `read-api` = (project in file("read-api"))
  .settings(commonSettings)
  .settings(
    name := "counter-read-api",
    libraryDependencies ++= Seq(
      // http4s
      "org.http4s" %% "http4s-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      // slick
      "com.typesafe.slick" %% "slick" % slickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
      "org.postgresql" % "postgresql" % postgresVersion,
    )
  )

lazy val `projection` = (project in file("projection"))
  .settings(commonSettings)
  .settings(
    name := "counter-projection",
    libraryDependencies ++= Seq(
      // persistence
      "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
      "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,
      // cassandra plugins
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "1.0.6",
      "software.aws.mcs" % "aws-sigv4-auth-cassandra-java-driver-plugin" % "4.0.6",
      // cluster
      "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      // projection
      "com.lightbend.akka" %% "akka-projection-eventsourced" % akkaProjectionVersion,
      "com.lightbend.akka" %% "akka-projection-cassandra" % akkaProjectionVersion,
      // logging
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion
    )
  ).dependsOn(`write-api`, `read-api`)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "counter",
    publish / skip := true
  )
  .aggregate(`write-api`, `read-api`, `projection`)
