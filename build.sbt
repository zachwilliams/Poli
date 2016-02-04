name := "Poli"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Spray repository" at "http://repo.spray.io",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1",
  "io.spray" %% "spray-can" % "1.3.3",
  "io.spray" %% "spray-routing" % "1.3.3",
  "io.spray" %% "spray-json" % "1.3.1"
)

