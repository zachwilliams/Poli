name := "Poli"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1"
)