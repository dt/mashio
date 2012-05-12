package net.reisub.mashio

import play.api.libs.json._

class Track(
  val baseIcon: String,
  val canDownloadAlbumOnly: Boolean = true,
  val iframeUrl: String,
  val artistUrl: String,
  val duration: Int,
  val album: String,
  val isClean: Boolean = false,
  val albumUrl: String,
  val shortUrl: String,
  val albumArtist: String,
  val canStream: Boolean = true,
  val embedUrl: String,
  val itemType: String,
  val price: String,
  val trackNum: Int,
  val albumArtistKey: String,
  val key: String,
  val icon: String,
  val canSample: Boolean = true,
  val name: String,
  val isExplicit: Boolean = false,
  val artist: String,
  val url: String,
  val albumKey: String,
  val artistKey: String,
  val canDownload: Boolean = true,
  val length: Int,
  val canTether: Boolean = true
)

object Track {
  implicit object TrackInfoFormat extends Format[Track] {
    def reads(json: JsValue): Track = new Track(
      baseIcon          = (json \ "baseIcon").asOpt[String].getOrElse(""),
      canDownloadAlbumOnly = (json \ "canDownloadAlbumOnly").asOpt[Boolean].getOrElse(true),
      iframeUrl         = (json \ "iframeUrl").asOpt[String].getOrElse(""),
      artistUrl         = (json \ "artistUrl").asOpt[String].getOrElse(""),
      duration          = (json \ "duration").asOpt[Int].getOrElse(1),
      album             = (json \ "album").asOpt[String].getOrElse(""),
      isClean           = (json \ "isClean").asOpt[Boolean].getOrElse(false),
      albumUrl          = (json \ "albumUrl").asOpt[String].getOrElse(""),
      shortUrl          = (json \ "shortUrl").asOpt[String].getOrElse(""),
      albumArtist       = (json \ "albumArtist").asOpt[String].getOrElse(""),
      canStream         = (json \ "canStream").asOpt[Boolean].getOrElse(true),
      embedUrl          = (json \ "embedUrl").asOpt[String].getOrElse(""),
      itemType          = (json \ "type").asOpt[String].getOrElse(""),
      price             = (json \ "price").asOpt[String].getOrElse(""),
      trackNum          = (json \ "trackNum").asOpt[Int].getOrElse(0),
      albumArtistKey    = (json \ "albumArtistKey").asOpt[String].getOrElse(""),
      key               = (json \ "key").asOpt[String].getOrElse(""),
      icon              = (json \ "icon").asOpt[String].getOrElse(""),
      canSample         = (json \ "canSample").asOpt[Boolean].getOrElse(true),
      name              = (json \ "name").asOpt[String].getOrElse(""),
      isExplicit        = (json \ "isExplicit").asOpt[Boolean].getOrElse(false),
      artist            = (json \ "artist").asOpt[String].getOrElse(""),
      url               = (json \ "url").asOpt[String].getOrElse(""),
      albumKey          = (json \ "albumKey").asOpt[String].getOrElse(""),
      artistKey         = (json \ "artistKey").asOpt[String].getOrElse(""),
      canDownload       = (json \ "canDownload").asOpt[Boolean].getOrElse(true),
      length            = (json \ "length").asOpt[Int].getOrElse(0),
      canTether         = (json \ "canTether").asOpt[Boolean].getOrElse(true)
    )

    def writes(t: Track): JsValue = JsObject(List(
      "baseIcon"        -> JsString(t.baseIcon),
      "canDownloadAlbumOnly"  -> JsBoolean(t.canDownloadAlbumOnly),
      "iframeUrl"       -> JsString(t.iframeUrl),
      "artistUrl"       -> JsString(t.artistUrl),
      "duration"        -> JsNumber(t.duration),
      "album"           -> JsString(t.album),
      "isClean"         -> JsBoolean(t.isClean),
      "albumUrl"        -> JsString(t.albumUrl),
      "shortUrl"        -> JsString(t.shortUrl),
      "albumArtist"     -> JsString(t.albumArtist),
      "canStream"       -> JsBoolean(t.canStream),
      "embedUrl"        -> JsString(t.embedUrl),
      "type"            -> JsString(t.itemType),
      "price"           -> JsString(t.price),
      "trackNum"        -> JsNumber(t.trackNum),
      "albumArtistKey"  -> JsString(t.albumArtistKey),
      "key"             -> JsString(t.key),
      "icon"            -> JsString(t.icon),
      "canSample"       -> JsBoolean(t.canSample),
      "name"            -> JsString(t.name),
      "isExplicit"      -> JsBoolean(t.isExplicit),
      "artist"          -> JsString(t.artist),
      "url"             -> JsString(t.url),
      "albumKey"        -> JsString(t.albumKey),
      "artistKey"       -> JsString(t.artistKey),
      "canDownload"     -> JsBoolean(t.canDownload),
      "length"          -> JsNumber(t.length),
      "canTether"       -> JsBoolean(t.canTether)
    ))
  }

  val youngBloodJson = """{"baseIcon": "album/a/e/7/00000000000a47ea/square-200.jpg", "canDownloadAlbumOnly": true, "iframeUrl": "http://rd.io/i/QVr9gjclDJA", "artistUrl": "/artist/The_Naked_And_Famous/", "duration": 246, "album": "Passive Me, Aggressive You", "isClean": false, "albumUrl": "/artist/The_Naked_And_Famous/album/Passive_Me%2C_Aggressive_You/", "shortUrl": "http://rd.io/x/QVr9gjclDJA", "albumArtist": "The Naked And Famous", "canStream": true, "embedUrl": "http://rd.io/e/QVr9gjclDJA", "type": "t", "price": "1.29", "trackNum": 7, "albumArtistKey": "r723387", "key": "t8019665", "icon": "http://cdn3.rd.io/album/a/e/7/00000000000a47ea/square-200.jpg", "canSample": true, "name": "Young Blood", "isExplicit": false, "artist": "The Naked And Famous", "url": "/artist/The_Naked_And_Famous/album/Passive_Me%2C_Aggressive_You/track/Young_Blood/", "albumKey": "a673770", "artistKey": "r723387", "canDownload": true, "length": 1, "canTether": true}"""
  val youngBlood = Json.parse(youngBloodJson).as[Track]
}