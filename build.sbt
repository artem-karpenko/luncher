organization := "ua.org.yozh"

name := "luncher"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-directives" % "0.7.1",
  "net.databinder" %% "unfiltered-filter" % "0.7.1",
  "net.databinder" %% "unfiltered-jetty" % "0.7.1",
  "org.squeryl" %% "squeryl" % "0.9.5-6",
  "com.h2database" % "h2" % "1.2.127"
)

resolvers ++= Seq(
  "java m2" at "http://download.java.net/maven/2"
)