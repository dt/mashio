package net.reisub.mashio.api

import com.twitter.finagle.http.Response

object Logger {
  def error(msg: => String) { println(Colors.error(msg)) }

  private var enableInfo: Boolean = true
  def infoOn: Boolean = enableInfo
  def setInfo(on: Boolean) { enableInfo = on }
  def info(msg: => String) { if (infoOn) println(msg) }

  private var enableDebug: Boolean = false
  def debugOn: Boolean = enableDebug
  def setDebug(on: Boolean) { enableDebug = on }
  def debug(msg: => String) { if (debugOn) println(msg) }

  private var enableTrace: Boolean = false
  def traceOn: Boolean = enableTrace
  def setTrace(on: Boolean) { enableTrace = on }
  def trace(msg: => String) { if (traceOn) println(msg) }

}

class ReqLogger(parent: ApiCallHandler) {
  def rpad(str: String, len: Int) = str + ( " " * (len - str.length))
  def handled(params: (String, String)*) {
    Logger.info(in(rpad(parent.method, 20) + "\t" + params.map{ case (a, b) => a + "=" + b}.mkString(" ")))
  }
  def info(msg: String) { Logger.info(line(msg)) }
  def debug(msg: String) { Logger.debug(line(msg)) }

  def panic {
    Logger.error(in(parent.method))
    params(log = Logger.error)
  }

  def unhandled {
    Logger.info(Colors.unhandled(in(parent.method)))
    params(color = Colors.unhandled)
  }

  def params(color: String => String = identity, log: (=> String) => Unit = Logger.debug) {
    log(color(in(parent.req.params.toString)))
  }

  def reply(resp: Response, log: (=> String) => Unit = Logger.trace) {
    log(out(resp.contentString))
  }

  def in(msg: String)    = "[%s]<-\t%s".format(parent.reqid, msg)
  def out(msg: String)   = "[%s]->\t%s".format(parent.reqid, msg)
  def line(msg: String)  = "[%s]  \t%s".format(parent.reqid, msg)
}
