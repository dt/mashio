package net.reisub.mashio.models

case class Album(_id: Album.Id,
	artist: Artist.Id,
	title: String,
	year: Option[String] = None,
	artwork: Option[String] = None,
	tracks: Set[Track.Id] = Set()
) extends {
  def setArtwork(path: String) =
    Album.dao.updateOne(_id, Query("$set" -> Query("artwork" -> path)))
 
  def fixTracks {
    val tracks = Track.dao.find(Query("album" -> _id)).map(_._id).toSet
    Album.dao.updateOne(_id, Query("$set" -> Query("tracks" -> tracks)))
  }
}

object Album extends MetaModel[Album]("album") {
  def findOrCreateByArtistAndTitle(artist: Artist.Id, title: String, year: => String): Album = {
    dao.upsert(Query("artist" -> artist, "title" -> title), Query("$set" -> Query("artist" -> artist, "title" -> title)))
    findByArtistAndTitle(artist, title).get
  }
  
  def findByArtistAndTitle(artistId: Artist.Id, title: String): Option[Album] =
    dao.findOne(Query("artist" -> artistId, "title" -> title))

  def addTrack(album: Id, track: Track.Id) = Album.dao.updateOne(album, Query("$addToSet" -> Query("tracks" -> track)))

}