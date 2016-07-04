name := """TeamTaskApp"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.3.6",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
)
