package controllers

import play.api._
import play.api.mvc._
import net.reisub.mashio.models._


object Browse extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def album(a: BoxedId[Artist], b: BoxedId[Album]) = Action {
   {for {
     artist <- Artist.db.findOneByID(a.id)
     album <- Album.db.findOneByID(b.id)
   } yield {
     val tracks = Track.db.findAll(album.tracks).toSeq.sortBy(_.trackNum)
     val artists = Artist.db.findAll(tracks.map(_.artist)).map(x => x._id -> x).toMap
     Ok(views.html.album(artist, album, tracks.map(x => DisplayTrack(x, artists.get(x.artist).getOrElse(artist)))))
   }}.getOrElse(NotFound)
  }

  def artist(artist: BoxedId[Artist]) = Action {
    Artist.db.findOneByID(artist.id).map{ artist =>
      Ok(views.html.artist(artist, Album.db.find(Query("artist" -> artist._id))))
    }.getOrElse(NotFound)
  }

  def addAlbum(artist: BoxedId[Artist]) = Action {
    Artist.db.findOneByID(artist.id).map{ artist =>
      Ok(views.html.artist(artist, Album.db.find(Query("artist" -> artist._id))))
    }.getOrElse(NotFound)
  }
}