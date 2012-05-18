package net.reisub.mashio.api

import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http._
import com.twitter.finagle.Service
import java.net.InetSocketAddress


object ProxyRunner {
  def main(args: Array[String]) {
    printGreet()
    print("starting rdio api proxy...")

    val proxy = new RdioProxy

    val server: Server = ServerBuilder()
      .codec(RichHttp[Request](Http()))
      .bindTo(new InetSocketAddress(4098))
      .name("rdio-proxy-server")
      .build(proxy)

    println(Console.GREEN +" started!"+Console.WHITE)

    printUsage()

    var running: Boolean = true
    while(running) {
      val key = System.in.read
      if (key == 'q')
        running = false
      else if (key == 'd')
        toggleDebug()
      else if (key == 'i')
        toggleInfo()
      else if (key == 'h')
        toggleInfo()
    }

    print("stopping api proxy...")
    server.close()
    println(" stopped.")
  }

  def toggleDebug() {
    if (debugOn) {
      debug("turning debug off...")
      setDebug(false)
    } else {
      setDebug(true)
      debug("turning debug on...")
    }
  }

  def toggleInfo() {
    if (infoOn) {
      info("turning info off...")
      setInfo(false)
    } else {
      setInfo(true)
      info("turning info on...")
   }
  }

  def printGreet() {
    println()
    println("#" * 40)
    println("#" + (" "*38) + "#")
    println("#" + (" "*16) + "mashio" + (" "*16) + "#")
    println("#" + (" "*38) + "#")
    println("#" * 40)
    println()
  }

  def printUsage() {
    println("Keys:")
    println("\tq\tquit")
    println("\td\ttoggle debug")
    println("\ti\ttoggle info")
  }
}