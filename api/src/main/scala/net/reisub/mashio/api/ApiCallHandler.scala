package net.reisub.mashio.api

import com.twitter.finagle.http._
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.{DefaultHttpResponse, HttpResponseStatus, HttpVersion}
import play.api.libs.json._
import net.reisub.mashio.api.RichJsObject.Implicits._
import net.reisub.mashio.models._


class ApiCallHandler(val reqid: String, val req: Request, fetch: => Future[Response]) {
  type Reply = Future[Response]
  val method = req.getParam("method")
  val log = new ReqLogger(this)

  def handle(): Reply = {
    val resp: Future[Response] = method match {
      // case "getAlbumsForArtist"   => getAlbumsForArtist
      case "getObjectFromUrl"     => call(getObjectFromUrl, "url")
      case "getPlaybackInfo"      => call(getPlaybackInfo, "key")
      case "getComments"          => call(filterNotInternal, "object")
      case "getRecentListeners"   => call(filterNotInternal, "key")
      case "addTrackPlay"         => call(addTrackPlay, "key")
      case callback if ApiCalls.PlaybackCallbacks(callback) =>
        call(playbackCallback(callback, _), "txid")

      case "jsError" => {
        log.panic
        Reply("{}")
      }
      case _ => {
        log.unhandled
        pass
      }
    }
    resp.foreach(log.reply(_))
    resp
  }

  private def call(f: String => Reply, paramName: String) = {
    val param = req.getParam(paramName)
    log.handled(paramName -> param)
    f(param)
  }

  private def pass: Future[Response] = {
    val reply = fetch
    if (Harmonizer.on) {
      val harmonizer = new Harmonizer(log)
      reply.map { resp =>
        val content = resp.contentString
        harmonizer.handle(method, content)
      }
    }
    reply
  }


  def getObjectFromUrl(rawUrl: String): Reply = {
    val url = if (rawUrl.startsWith("/")) rawUrl else "/" + rawUrl

    val AlbumPattern = "^/artist/[^/]+/album/[^/]+/$".r
    val PlaylistPattern = "^/people/[^/]+/playlists.*".r

    url match {
      case AlbumPattern()     => getAlbumFromUrl(url)
      case PlaylistPattern()  => getPlaylistFromUrl(url)
      case _  => { log.debug("unknown url: " + url); pass }
    }
  }

  def getAlbumFromUrl(url: String) = {
    pass
  }

  def getPlaylistFromUrl(url: String) = {
    if (url == "/people/state_machine/playlists/785247/more_like_this/") {
      def secondTrack(id: Album.Id) = {
        val album = Album.db.findOneByID(id).get
        val artist = Artist.db.findOneByID(album.artist).get
        val track = Track.db.findOneByID(album.tracks.tail.head).get
        ApiTrack.fromTrack(DisplayTrack(track, artist, album))
      }
      val beach = secondTrack(TaggedId("4fb9a6c7d7f57ab2706d3b8b"))
      val pigpen = secondTrack(TaggedId("4fb9b423d7f57ab2706d3b9b"))

      fetch.map { resp =>
        for {
          json <- Json.parse(resp.contentString).asOpt[JsObject]
          total <- (json \ "result" \ "tracks" \ "total").asOpt[Int]
          items <- (json \ "result" \ "tracks" \ "items").asOpt[List[JsValue]]
          val newItems = Json.toJson(beach) :: Json.toJson(pigpen) :: items
          withNewItems <- (json / "result" / "tracks" / "items").setTo(Json.toJson(newItems))
          withNewCount <- (withNewItems / "result" / "tracks" / "total").setTo(JsNumber(newItems.size))
        }
          Reply.setContent(resp, withNewCount)
        resp
      }
    } else pass
  }

  def addTrackPlay(key: String): Reply = {
    if (isInternal(key))
      Reply("{\"txid\": \"" + TxId.generate + "\"}")
    else
      pass
  }

  def playbackCallback(name: String, txid: String): Reply = {
    if (TxId.isInternal(txid))
      Reply("{}")
    else
      pass
  }

  def filterNotInternal(key: String): Reply = {
    if (isInternal(key))
      Reply("""{"items": [], "type": "list"}""")
    else
      pass
  }

  def getPlaybackInfo(key: String): Reply = {
    val format = req.getParam("type")

    Track.findByRdioId(RdioId[Track](key))
      .filterNot(_.rdio.canStream)
      .flatMap(track => PlaybackInfo.forTrack(track, format))
      .map(js => Reply(js))
      .getOrElse(pass)
  }

  // Response helpers, for logging respose bodies and generating create responses
  object Reply {
    def apply(json: JsValue): Reply = apply(getJsonString(json))
    def apply(json: String): Reply = {
      val response = Response(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK))
      response.setContentTypeJson
      setContent(response, """{"status": "ok", "result": """+json+""", "clientVersion": 1335572034}""")
      Future.value(response)
    }

    def setContent(resp: Response, content: JsValue) { setContent(resp, getJsonString(content)) }
    def setContent(resp: Response, content: String) {
      resp.contentString = content
      resp.contentLength = resp.getContent.readableBytes()
    }

  }

  def isInternal(key: String) = {
    if (key.startsWith("t")) {
      Track.findByRdioId(RdioId[Track](key)).exists(_.rdio.isFake)
    } else if (key.startsWith("r")) {
      Artist.findByRdioId(RdioId[Artist](key)).exists(_.rdio.isFake)
    } else if (key.startsWith("a")) {
      Album.findByRdioId(RdioId[Album](key)).exists(_.rdio.isFake)
    } else if (key.startsWith("p"))
      false
    else {
      log.info("unknown key: "+key)
      false
    }
  }
}


object ApiCalls {
  val PlaybackCallbacks = Set(
    "addSongFinished",
    "addSongPaused",
    "addSongResumed",
    "addSongSkippedTime",
    "addTimedPlayInformation"
   )
}