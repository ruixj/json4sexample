name := "testjson4s"

version := "0.1"

scalaVersion := "2.12.6"
// avro
val avro_version = "1.7.6-cdh5.5.2"
lazy val avro = "org.apache.avro" % "avro" % avro_version

lazy val json4s = "org.json4s" %% "json4s-jackson" % "3.2.11"
libraryDependencies += json4s
libraryDependencies += avro