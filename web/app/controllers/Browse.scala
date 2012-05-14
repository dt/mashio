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
     artist <- Artist.dao.findOneByID(a.id)
     album <- Album.dao.findOneByID(b.id)
   } yield {
     val tracks = Track.dao.findAll(album.tracks).toSeq.sortBy(_.trackNum)
     val artists = Artist.dao.findAll(tracks.map(_.artist)).map(x => x._id -> x).toMap
     Ok(views.html.album(artist, album, tracks.map(x => DisplayTrack(x, artists.get(x.artist).getOrElse(artist)))))	
   }}.getOrElse(NotFound)
  }
  
  def artist(artist: BoxedId[Artist]) = Action {
    Artist.dao.findOneByID(artist.id).map{ artist => 
      Ok(views.html.artist(artist, Album.dao.find(Query("artist" -> artist._id))))
    }.getOrElse(NotFound)
  }
  
  def addAlbum(artist: BoxedId[Artist]) = Action {
    Artist.dao.findOneByID(artist.id).map{ artist => 
      Ok(views.html.artist(artist, Album.dao.find(Query("artist" -> artist._id))))
    }.getOrElse(NotFound)
  }
}