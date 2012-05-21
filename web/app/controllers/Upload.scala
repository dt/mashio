package controllers

import java.io.File
import play.api._
import play.api.mvc._
import net.reisub.mashio.models._
import net.reisub.mashio.TryOpt
import java.io.File
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.{FieldKey, KeyNotFoundException}
import javax.imageio.ImageIO


object Upload extends Controller {
  val trackPath = new File("uploads/tracks")
  val artPath = new File("uploads/art")

  def index = Action {
    Ok(views.html.upload())
  }

  def upload = Action(parse.temporaryFile) { request =>
    try {
    	val oldName = request.headers.get("x-file-name").get
    	Logger.debug("Name:\t" + oldName)
    	val ext = oldName.split('.').last
    	ext match {
    	  case "mp3" => handleMp3(request)
    	}
    } catch {
      case e => {
        Logger.error("upload failed", e)
        Ok("{success: false, failure: '" + e + "' }").as("application/json")
      }
    }
  }

  def handleMp3(request: play.api.mvc.Request[play.api.libs.Files.TemporaryFile]) = {
    val id = Track.newId
	val fileName =  id+".mp3"
	val filePath = new File(trackPath, fileName)
    try {
	    request.body.moveTo(filePath)
	  	val tagger = AudioFileIO.read(filePath)
	  	val tag = tagger.getTag
		val artist = Artist.findOrCreateByName(tag.getFirst(FieldKey.ARTIST))
		val albumArtist = Artist.findByName(tag.getFirst(FieldKey.ALBUM_ARTIST)).getOrElse(artist)
		val album = Album.findOrCreateByArtistAndTitle(albumArtist,
        tag.getFirst(FieldKey.ALBUM),
        tag.getFirst(FieldKey.YEAR))

		if (album.artwork.isEmpty) Option(tag.getFirstArtwork).foreach{ art =>
		  try {
		    val dest = album._id + ".png"
		    ImageIO.write(art.getImage, "png", new File(artPath, dest))
		    album.setLocalArtwork(dest)
		  } catch {
		    case e => Logger.error("saving artwork failed", e)
		  }
		}


		val title = tag.getFirst(FieldKey.TITLE)
		val length = TryOpt(classOf[NumberFormatException], classOf[KeyNotFoundException])(tagger.getAudioHeader.getTrackLength)
		val trackNum = TryOpt(classOf[NumberFormatException], classOf[KeyNotFoundException])(tag.getFirst(FieldKey.TRACK).toInt)


		Track.findByArtistAlbumAndTitle(artist._id, album._id, title) match {
	      case Some(_) => {
	        Ok("{success: false, failure: 'dupe' }").as("application/json")
	      }
	      case None => {
	        Logger.debug("Ading track: %s (%s)".format(title, id))
	        Logger.debug("\tArtist:\t%s (%s)".format(artist.name, artist._id))
	        Logger.debug("\tAlbum:\t%s (%s)".format(album.title, album._id))
	        Track.add(artist, album, title, trackNum.getOrElse(0), length.getOrElse(0), Some(fileName), id)
	        Ok("{success: true}").as("application/json")
	      }
	    }
    } catch {
    	case e => {
    	  filePath.delete()
    	  throw e
    	}
    }

  }
}