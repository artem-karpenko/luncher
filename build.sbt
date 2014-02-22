organization := "ua.org.yozh"

name := "luncher"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.2"

mainClass := Some("ua.org.yozh.Server")

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-directives" % "0.7.1",
  "net.databinder" %% "unfiltered-filter" % "0.7.1",
  "net.databinder" %% "unfiltered-jetty" % "0.7.1",
  "org.squeryl" %% "squeryl" % "0.9.5-6",
  "com.h2database" % "h2" % "1.2.127",
  "io.spray" %% "spray-json" % "1.2.5",
  "javax.mail" % "mail" % "1.4.7",
  "org.quartz-scheduler" % "quartz" % "2.2.1",
  "org.quartz-scheduler" % "quartz-jobs" % "2.2.1",
  "org.slf4j" % "slf4j-simple" % "1.7.6",
  "joda-time" % "joda-time" % "2.3", // TODO consider nscala-time
  "org.joda" % "joda-convert" % "1.2"
)

resolvers ++= Seq(
  "java m2" at "http://download.java.net/maven/2",
  "spray" at "http://repo.spray.io/"
)