package net.reisub.mashio

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http._
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import java.net.InetSocketAddress
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import play.api.libs.json._

class RdioProxy extends Service[Request, Response] {
  def baseClient(host: String) = ClientBuilder().hosts(host).hostConnectionLimit(2).codec(Http()).build()
  val rdioWeb: Service[HttpRequest, HttpResponse] = baseClient("rdio.com:80")
  val rdioApi: Service[HttpRequest, HttpResponse] = baseClient("rdio.api.mashery.com:80")

  def clientFor(request: Request) = if (request.getHeader("Host") == "api.rdio.com") rdioApi else rdioWeb
  def isApi(request: Request) = clientFor(request) == rdioApi || request.path.startsWith("/api/")
  def fetchRdioResp(request: Request) = clientFor(request)(request).map(Response(_))

  def apply(request: Request) = {
    if (isApi(request)) {
      try {
        ApiCallHander.handleApiReq(request, fetchRdioResp _)
      } catch {
        case e: Exception => {
          println(Console.RED + e.getClass.getName + Console.WHITE)
          e.printStackTrace
          fetchRdioResp(request)
        }
      }
    } else {
      debug(Console.BLUE + "[ passing "+request.path+" thorugh ]" + Console.WHITE)
      fetchRdioResp(request)
    }
  }
}