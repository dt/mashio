package net.reisub.mashio.models

case class Track(_id: Track.Id,
	artist: Artist.Id,
	album: Album.Id,
	title: String,
	trackNum: Int,
	length: Int,
	path: Option[String] = None,
  rdio: RdioData[Track]
) {
  lazy val getAlbum = Album.db.findOneByID(album)
  lazy val getArtist = Artist.db.findOneByID(artist)
}

case class DisplayTrack(track: Track, artist: Artist, album: Album)

object Track extends MetaModel[Track]("track") {
  case class ListItem(track: Track.Id, pos: Int)

  def findByArtistAlbumAndTitle(artistId: Artist.Id, albumId: Album.Id, title: String) =
    db.findOne(Query("artist" -> artistId, "album" -> albumId, "title" -> title))

  def add(artist: Artist, album: Album, title: String,
          trackNum: Int, length: Int, path: Option[String],
          id: Track.Id = TaggedId.get[Track]): Track = {
    val track = Track(id, artist._id, album._id, title, trackNum, length, path,
        RdioData(
            Util.keyStr[Track]("t"),
            Util.urlStr(album.rdio.url + "track/", title),
            isFake = true
        ))
  	db.save(track)

    if (!artist.hasFakes)
      artist.setFakes(true)

  	Album.addTrack(album._id, track._id, fake = true)
  	track
  }


  def harmonize(artist: Artist.Id, album: Album.Id, title: String,
                trackNum: Int, length: Int,
                rdioId: RdioId, rdioUrl: String, canStream: Boolean): Track = {
    db.upsert(
      Query("artist" -> artist, "album" -> album, "title" -> title),
      Query("$set" -> Query(
        "artist" -> artist,
        "album" -> album,
        "title" -> title,
        "trackNum" -> trackNum,
        "length" -> length,
        "rdio.key" -> rdioId,
        "rdio.url" -> rdioUrl,
        "rdio.canStream" -> canStream
      ))
    )
    val track = findByRdioId(rdioId).get
    Album.addTrack(album, track._id, fake = false)
    track

  }


  def findByRdioUrl(url: String): Option[Track] =
    db.findOne(Query("rdio.url" -> url))

  def findByRdioId(id: RdioId): Option[Track] =
    db.findOne(Query("rdio.key" -> id))
}