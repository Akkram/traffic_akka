name := "gestion_traffic_akka"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test,
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "1.0",
  "com.typesafe.akka" %%"akka-http-testkit-experimental" % "1.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)