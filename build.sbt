
name := "event-router"

version := "1.0.0"

scalaVersion := "2.12.6"

enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "2.3.0",
  "org.apache.kafka" %% "kafka-streams-scala" % "2.3.0",
  "org.rocksdb" % "rocksdbjni" % "5.14.2",
  "org.slf4j" % "slf4j-log4j12" % "1.7.25",
  "javax.ws.rs" % "javax.ws.rs-api" % "2.1" artifacts Artifact("javax.ws.rs-api", "jar", "jar"),
  "org.json4s" %% "json4s-jackson" % "3.6.0",

)


packageName in Docker := s"gcr.io/${sys.env.get("GCP_PROJECT").get}/event-router"
version in Docker := "1911.1"
