package net.reisub.mashio.models

case class Track(_id: Track.Id,
	artist: Artist.Id,
	album: Album.Id,
	title: String,
	trackNum: Option[Int],
	length: Option[Int],
	path: Option[String]
)

case class DisplayTrack(track: Track, artist: Artist)

object Track extends MetaModel[Track]("track") {
  case class ListItem(track: Track.Id, pos: Int)

  def findByArtistAlbumAndTitle(artistId: Artist.Id, albumId: Album.Id, title: String) =
    db.findOne(Query("artist" -> artistId, "album" -> albumId, "title" -> title))

  def add(artist: Artist.Id, album: Album.Id, title: String, trackNum: Option[Int],
          length: Option[Int], path: Option[String], id: Track.Id = TaggedId.get[Track]): Track = {
    val track = Track(id, artist, album, title, trackNum, length, path)
  	db.save(track)
  	Album.addTrack(album, track._id)
  	track
  }
}