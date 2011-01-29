package com.example

import org.clapper.avsl.Logger

/** oauth client */
object Client {
  val log = Logger(Client.getClass)
  val port = 8081
  val consumer = dispatch.oauth.Consumer("key", "secret")
  def resources = new java.net.URL(getClass.getResource("/web/robots.txt"), ".")

  def main(args: Array[String]) {
    log.info("starting unfiltered oauth consumer at localhost on port %s" format port)
    unfiltered.jetty.Http(port)
      .resources(Client.resources)
      .filter(new App(consumer)).run
  }
}
