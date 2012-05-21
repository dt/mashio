package net.reisub.mashio.api

import net.reisub.mashio.models._
import play.api.libs.json._

object Harmonizer {
  def on = isOn
  var isOn = true
}

class Harmonizer(logger: ReqLogger) {
  def handle(method: String, reply: String) {
    Json.parse(reply).asOpt[JsObject].map{ json =>
      method match {
        case "getAlbumsForArtist" => {
          for {
            albums <- (json \ "result" \ "items").asOpt[List[JsObject]]
            albumJson <- albums
          } album(albumJson)
        }
        case "getObjectFromUrl" => {
          logger.debug("Harmonizing getObjectFromUrl")
          for {
            albumJson   <- (json \ "result").asOpt[JsObject]
            "a"         <- (json \ "result" \ "type").asOpt[String]
            album       <- album(albumJson)
          } trackList(album, albumJson)
        }
      }
    }.getOrElse{
      logger.debug("harmonizer could not parse response")
    }
  }


  def album(json: JsObject): Option[Album] = if (Harmonizer.on) {
    for {
      artistKey   <- (json \ "artistKey").asOpt[String]
      albumKey    <- (json \ "key").asOpt[String]
      albumTitle  <- (json \ "name").asOpt[String]
      artistName  <- (json \ "artist").asOpt[String]
      artistUrl   <- (json \ "artistUrl").asOpt[String]
      albumUrl    <- (json \ "url").asOpt[String]
      icon        <- (json \ "icon").asOpt[String]
      date        <- (json \ "displayDate").asOpt[String]
      canStream   <- (json \ "canStream").asOpt[Boolean]
    } yield {
      logger.info("Harmonizing Artist: %s [%s]".format(artistName, artistKey))
      val artist = Artist.harmonize(artistName, RdioId[Artist](artistKey), artistUrl)
      logger.info("Harmonizing Album: %s [%s]".format(albumTitle, albumKey, albumUrl))
      Album.harmonize(artist._id, albumTitle, RdioId[Album](albumKey),
          rdioUrl = albumUrl, icon, date, canStream)
    }
  } else None

  def trackList(album: Album, json: JsObject): Seq[Track] = {
    if (Harmonizer.on) {
      logger.debug("Harmonizing track list for " + album._id)
      for {
        trackJson   <- (json \ "tracks" \ "items").asOpt[List[JsObject]].flatten
        artistUrl   <- (trackJson \ "artistUrl").asOpt[String]      // "/artist/Beach_House/",
        length      <- (trackJson \ "duration").asOpt[Int]          // 325,
        canStream   <- (trackJson \ "canStream").asOpt[Boolean]     // true,
        trackNum    <- (trackJson \ "trackNum").asOpt[Int]          // 7,
        trackKey    <- (trackJson \ "key").asOpt[String]            // "t17035932",
        title       <- (trackJson \ "name").asOpt[String]           // "New Year",
        artistName  <- (trackJson \ "artist").asOpt[String]         // "Beach House",
        trackUrl    <- (trackJson \ "url").asOpt[String]            // "/artist/Beach_House/album/Bloom/track/New_Year/",
        artistKey   <- (trackJson \ "artistKey").asOpt[String]      // "r84798"
      } yield {
        logger.info("Harmonizing Artist: %s [%s]".format(artistName, artistKey))
        val artist = Artist.harmonize(artistName, RdioId[Artist](artistKey), artistUrl)
        logger.info("Harmonizing Track: %s [%s]".format(title, trackKey))
        Track.harmonize(artist._id, album._id, title, trackNum, length,
              RdioId[Track](trackKey), trackUrl, canStream)
      }
    }.toSeq else Nil
  }
}