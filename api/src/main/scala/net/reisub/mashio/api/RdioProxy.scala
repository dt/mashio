package net.reisub.mashio.api

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http._
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import java.net.InetSocketAddress
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import play.api.libs.json._
import scala.util.Random

class RdioProxy extends Service[Request, Response] {
  def baseClient(host: String) = ClientBuilder().hosts(host).hostConnectionLimit(2).codec(Http()).build()


  val rdioWeb: Service[HttpRequest, HttpResponse] = baseClient("rdio.com:80")
  val rdioMobile: Service[HttpRequest, HttpResponse] = baseClient("m.rdio.com:80")
  val rdioApi: Service[HttpRequest, HttpResponse] = baseClient("rdio.api.mashery.com:80")

  def clientFor(request: Request) = request.getHeader("Host") match {
    case "api.rdio.com" => rdioApi
    case "m.rido.com" => rdioMobile
    case _ => rdioWeb
  }

  def isApi(request: Request) = clientFor(request) == rdioApi || request.path.startsWith("/api/")
  def fetchRdioResp(request: Request) = clientFor(request)(request).map(Response(_))

  def apply(request: Request) = {
    if (isApi(request)) {
      try {
        val randStr = "%03d".format(Random.nextInt(999))
        new ApiCallHandler(randStr, request, fetchRdioResp(request)).handle()
      } catch {
        case e: Exception => {
          Logger.error(e.getClass.getName)
          Logger.error({e.printStackTrace; ""})
          fetchRdioResp(request)
        }
      }
    } else {
      Logger.debug(Colors.web("[ passing "+request.path+" thorugh ]"))
      fetchRdioResp(request)
    }
  }
}