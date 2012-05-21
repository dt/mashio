package net.reisub.mashio.models

case class Album(_id: Album.Id,
	artist: Artist.Id,
	title: String,
	releaseDate: Option[String] = None,
	artwork: Option[AlbumArtOptions] = None,
	tracks: Set[Track.Id] = Set(),
  rdio: RdioData[Album],
  hasFakes: Boolean = false
) {
  lazy val getArtist = Artist.db.findOneByID(artist)

  def getArtwork: Option[Album.Art] = artwork.flatMap { art =>
    art.local.map(Album.LocalArt).orElse(art.rdio.map(Album.RdioArt))
  }

  def setLocalArtwork(path: String) =
    Album.db.updateOne(_id, Query("$set" -> Query("artwork.local" -> path)))

  def fixTracks {
    val tracks = Track.db.find(Query("album" -> _id)).map(_._id).toSet
    Album.db.updateOne(_id, Query("$set" -> Query("tracks" -> tracks)))
  }
}

case class AlbumArtOptions(local: Option[String] = None, rdio: Option[String] = None)

object Album extends MetaModel[Album]("album") {
  sealed trait Art
  case class LocalArt(path: String) extends Art
  case class RdioArt(uri: String) extends Art

  def findOrCreateByArtistAndTitle(artist: Artist, title: String, releaseDate: => String): Album = {
    db.upsert(Query("artist" -> artist._id, "title" -> title),
        Query("$set" -> Query(
          "artist" -> artist._id,
          "title" -> title,
          "rdio.key" -> Util.keyStr("a"),
          "rdio.url" ->  Util.urlStr(artist.rdio.url + "album/", title),
          "rdio.isFake" -> true
        )))

    if (!artist.hasFakes)
      artist.setFakes(true)

    findByArtistAndTitle(artist._id, title).get
  }

  def harmonize(artist: Artist.Id, title: String, rdioId: RdioId, rdioUrl: String,
    rdioIcon: String, releaseDate: String, canStream: Boolean): Album = {
    db.upsert(
      Query("artist" -> artist, "title" -> title),
      Query("$set" -> Query(
        "artist" -> artist,
        "title" -> title,
        "artwork.rdio" -> rdioIcon,
        "releaseDate" -> releaseDate,
        "rdio.key" -> rdioId,
        "rdio.url" -> rdioUrl,
        "rdio.canStream" -> canStream
      ))
    )
    findByRdioId(rdioId).get
  }

  def findByRdioId(id: RdioId): Option[Album] =
    db.findOne(Query("rdio.key" -> id))

  def findByRdioUrl(url: String): Option[Album] =
    db.findOne(Query("rdio.url" -> url))

  def findByArtistAndTitle(artist: Artist.Id, title: String): Option[Album] =
    db.findOne(Query("artist" -> artist, "title" -> title))

  def addTrack(album: Id, track: Track.Id, fake: Boolean) = {
    val clauses = "$addToSet" -> Query("tracks" -> track) ::
      (if (fake) "$set" -> Query("hasFakes" -> true) :: Nil else Nil)
    Album.db.updateOne(album, Query(clauses: _*))
  }
}