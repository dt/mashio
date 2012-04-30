package net.reisub.mashio

import com.twitter.finagle.http._
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.{DefaultHttpResponse, HttpResponseStatus, HttpVersion}
import play.api.libs.json._
import scala.util.Random
import net.reisub.mashio.RichJsObject.Implicits._


object ApiCallHander {
  object Colors {
    val all = Set(unhandled, error, good)
    val unhandled = Console.CYAN
    val error = Console.RED
    val good = Console.GREEN
  }

  val PlaybackCallbacks = Set(
    "addSongFinished",
    "addSongPaused",
    "addSongResumed",
    "addSongSkippedTime",
    "addTimedPlayInformation"
   )

  def handleApiReq(request: Request, respF: Request => Future[Response]): Future[Response] = {
    val method = request.getParam("method")

    method match {
      case "getObjectFromUrl" =>
        getObjectFromUrl(request, respF)

      case "getComments" =>
        getComments(request, respF)

      case "addTrackPlay" =>
        addTrackPlay(request, respF)

      case callback if PlaybackCallbacks(callback) =>
        playbackCallback(callback, request, respF)

      case "jsError" => {
        val reqid = Log("jsError", request, Colors.error, error _)
        Log.params(reqid, request, Colors.error, error _)
        Resp.dummy(reqid, "{}")
      }

      case other => {
        val reqid = Log(other, request, Colors.unhandled)
        Log.params(reqid, request, Colors.unhandled)
        Resp(reqid, respF(request))
      }
    }
  }

  def getObjectFromUrl(request: Request, respF: Request => Future[Response]): Future[Response] = {
    val reqid = Log("getObjectFromUrl", request)
    val url = request.getParam("url")

    if (url == "people/state_machine/playlists/785247/more_like_this/") {
      Resp(reqid, respF(request).map{ resp =>
        val body = resp.contentString
        for {
          json <- Json.parse(body).asOpt[JsObject]
          total <- (json \ "result" \ "tracks" \ "total").asOpt[Int]
          items <- (json \ "result" \ "tracks" \ "items").asOpt[List[JsValue]]
          val newItems = Json.toJson(Track.youngBlood) :: items
          withNewItems <- (json / "result" / "tracks" / "items").setTo(Json.toJson(newItems))
          withNewCount <- (withNewItems / "result" / "tracks" / "total").setTo(JsNumber(newItems.size))
        } {
          val jsonString = getJsonString(withNewCount)
          Resp.setContent(resp, jsonString)
        }
        resp
      })
    } else {
      Resp(reqid, respF(request))
    }
  }

  def getComments(request: Request, respF: Request => Future[Response]): Future[Response] = {
    val obj = request.getParam("object")

    val reqid = Log("getComments "+obj, request)

    if (Helpers.isInternal(obj))
      Resp.dummy(reqid, """{"items": [], "type": "list"}""")
    else
      Resp(reqid, respF(request))
  }

  def addTrackPlay(request: Request, respF: Request => Future[Response]): Future[Response] = {
    val key = request.getParam("key")
    val reqid =  Log("addTrackPlay "+key, request)
    if (Helpers.isInternal(key))
      Resp.dummy(reqid, "{\"txid\": \"" + Helpers.generateInternalTxId + "\"}")
    else
      Resp(reqid, respF(request))
  }

  def playbackCallback(name: String, request: Request, respF: Request => Future[Response]): Future[Response] = {
    val reqid = Log(name, request)
    val txid = request.getParam("txid")
    if (Helpers.isInternalTxId(txid))
      Resp.dummy(reqid, "{}")
    else
      Resp(reqid, respF(request))
  }

  // Helpers are organized into objects, because I felt like it.

  // Response helpers, for logging respose bodies and generating dummy responses
  object Resp {
    def apply(reqid: String, respF: Future[Response]): Future[Response] = respF.map { resp =>
      debug(reqid + " ->\t" + resp.contentString)
      resp
    }

    def dummy(reqid: String, json: String) = {
      val response = Response(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK))
      response.setContentTypeJson
      response.contentString = """{"status": "ok", "result": """+json+""", "clientVersion": 1335572034}"""
      apply(reqid, Future.value(response))
    }

    def setContent(resp: Response, content: String) {
      resp.contentString = content
      resp.contentLength = resp.getContent.readableBytes()
    }
  }

  object Log {
    def apply(name: String, request: Request, color: String = Colors.good, f: (=> String) => Unit = info _): String = {
      val reqid = Helpers.genReqId
      f(color + reqid + " <-\t" + name + Console.WHITE)
      reqid
    }

    def params(reqid: String, request: Request, color: String = Colors.good, f: (=> String) => Unit = debug _) = {
      f(color + reqid + "<- \t" + request.getParams.toString + Console.WHITE)
    }
  }

  object Helpers {
    def genReqId: String = "[%03d]".format(Random.nextInt(999))

    def isInternal(key: String) = false

    def generateInternalTxId =
      "deadbeef-%04d-%04d-%04d-c0ffeec0ffee".format(Random.nextInt(9999), Random.nextInt(9999), Random.nextInt(9999))

    def isInternalTxId(id: String) =
      id.startsWith("deadbeef") && id.endsWith("c0ffeec0ffee")
    }
}