import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  val uf_version = "0.3.1"
  val db_version = "0.7.8"

  // unfiltered
  val uff = "net.databinder" %% "unfiltered-filter" % uf_version
  val ufj = "net.databinder" %% "unfiltered-jetty" % uf_version
  val ufjs = "net.databinder" %% "unfiltered-json" % uf_version

  val dljs = "net.databinder" %% "dispatch-lift-json" % db_version
  val djs = "net.databinder" %% "dispatch-json" % db_version
  val doa = "net.databinder" %% "dispatch-oauth" % db_version

  // logging
  val javaNetRepo = "Java.net Repository for Maven" at "http://download.java.net/maven/2"
  val newReleaseToolsRepository = ScalaToolsSnapshots
  val avsl = "org.clapper" %% "avsl" % "0.3.1"
}
