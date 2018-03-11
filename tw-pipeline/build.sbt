
scalaVersion := "2.11.8" // can't find some spark deps on 2.12

val sparkVersion = "2.3.0"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.thoughtworks.cd.de",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),

    name := "tw-pipeline",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.5",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test",
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion,
      "com.amazonaws" % "aws-java-sdk" % "1.10.47" exclude("com.fasterxml.jackson.core", "jackson-databind"),
      "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.7",
      "com.typesafe" % "config" % "1.3.2",
      "org.apache.spark" %% "spark-streaming" % sparkVersion,
      "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion, // don't depend on kafka-clients directly
      "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion
    )
  )
