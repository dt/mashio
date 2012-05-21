package net.reisub.mashio.api

import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http._
import com.twitter.finagle.Service
import java.net.InetSocketAddress

import net.reisub.mashio.models._


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

    printSettings()

    printUsage()

    val questions = Set[Int]('r')
    var running: Boolean = true
    var last: Int = ' '
    while(running) {
      if (!questions.contains(last))
        println("mashio>")
      val keyPress: Int = System.in.read
      keyPress match {
        case 'q' => running = false
        case 'h' => printUsage()
        case 's' => printSettings()
        case 't' => toggleTrace()
        case 'd' => toggleDebug()
        case 'i' => toggleInfo()
        case 'g' => toggleGathering()
        case 'a' => printArtists()

        case 'r' => print(Console.RED + Console.BLINK + "clear db? [y/n]" + Console.RESET)
        case 'n' if last == 'r' => println("  n")
        case 'y' if last == 'r' => clearDb()

        case _ => printUsage()
      }
      last = keyPress
    }

    print("stopping api proxy...")
    server.close()
    println(" stopped.")
  }

  def toggleDebug() {
    if (Logger.debugOn) {
      Logger.info("turning debug off...")
      Logger.setDebug(false)
    } else {
      Logger.setDebug(true)
      Logger.info("turning debug on...")
    }
  }

  def toggleTrace() {
    if (Logger.traceOn) {
      Logger.info("turning trace off...")
      Logger.setTrace(false)
    } else {
      Logger.setTrace(true)
      Logger.info("turning trace on...")
    }
  }

  def toggleInfo() {
    if (Logger.infoOn) {
      Logger.info("turning Logger.info off...")
      Logger.setInfo(false)
    } else {
      Logger.setInfo(true)
      Logger.info("turning Logger.info on...")
   }
  }

  def toggleGathering() {
    if (Harmonizer.on) {
      Logger.info("turning gathering off...")
      Harmonizer.isOn = false
    } else {
      Harmonizer.isOn = true
      Logger.info("turning gathering on...")
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

  def bold(s: String): String = Console.BOLD + s + Console.RESET
  def bold(s: Boolean): String = if (s) Console.BOLD + s.toString + Console.RESET else s.toString

  def printSettings() {
    println()
    println("Current Settings:")
    println("    data gathering:\t" + bold(Harmonizer.on))
    println("    info printing:\t"  + bold(Logger.infoOn))
    println("    debug printing:\t" + bold(Logger.debugOn))
    println("    trace printing:\t" + bold(Logger.traceOn))
    println()
  }

  def printUsage() {
    def usage(key: String, desc: String) = ("    %s\t%s").format(bold(key), desc)
    println("Keys:")
    println(usage("q","quit"))
    println(usage("t","toggle trace"))
    println(usage("d","toggle debug"))
    println(usage("i","toggle info"))
    println(usage("g","toggle gathering"))
    println(usage("s","print settings"))
    println(usage("a","print artist lists [warning: could be slow]"))
    println(usage("r","clear database [!!!]"))
    println()
  }

  def printArtists() {
    val artists = Artist.db.findAll().toList
    println("Artists (" + artists.size + "): ")
    artists.foreach{ x =>
      val rdid = "[" + x.rdio.key + "]"
      println("    " + x.name + "\t" + rdid)
    }
    println()
  }

  def clearDb() {
    println()
    println(Console.YELLOW + "Clearing database...")
    MetaModel.ALL.foreach{ m =>
      println("\t" + m.collectionName + "...")
      m.clearAll()
    }
    println("done.")
    println(Console.RESET)
  }
}