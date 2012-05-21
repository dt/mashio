package net.reisub.mashio.api

import play.api.libs.json._
import net.reisub.mashio.models._

object PlaybackInfo {


  def forTrack(track: Track, format: String): Option[JsValue] = {
    implicit def toJsBool(x: Boolean): JsBoolean = JsBoolean(x)
    implicit def toJsString(x: String): JsString = JsString(x)
    for {
      path <- track.path
      album <- track.getAlbum
      albumArtist <- album.getArtist
      artist <- track.getArtist
    } yield {

      val iconUrl = ApiTrack.iconUrl(album)
      val surl = ApiTrack.streamUrl(track)
      val url = ApiTrack.shortUrl(track._id)

      JsObject(Seq[(String, JsValue)](
        "baseIcon"          -> ApiTrack.DefaultBaseIcon,
        "userHasUnlimited"  -> true,
        "canDownloadAlbumOnly"  -> false,
        "artistUrl"         -> artist.rdio.url,
        "duration"          -> JsNumber(track.length),
        "album"             -> album.title,
        "isClean"           -> false,
        "albumUrl"          -> album.rdio.url,
        "shortUrl"          -> url,
        "albumArtist"       -> albumArtist.name,
        "canStream"         -> true,
        "embedUrl"          -> url,
        "type"              -> "t",
        "price"             -> "None",
        "surl"              -> surl,
        "trackNum"          -> JsNumber(track.trackNum),
        "albumArtistKey"    -> albumArtist.rdio.key,
        "key"               -> track.rdio.key,
        "icon"              -> iconUrl,
        "canSample"         -> true,
        "name"              -> track.title,
        "isExplicit"        -> false,
        "artistKey"         -> artist.rdio.key,
        "url"               -> track.rdio.url,
        "albumKey"          -> album.rdio.key,
        "artist"            -> artist.name,
        "canDownload"       -> false,
        "length"            -> JsNumber(1),
        "canTether"         -> false
      ))
    }
  }
}