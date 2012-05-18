package net.reisub.mashio.models

case class Artist(_id: Artist.Id, name: String, rdioId: Option[RdioId]) {
  def mergeInto(other: Artist.Id) {
    Album.db.updateMulti(Query("artist" -> _id), Query("$set" -> Query("artist" -> other)))
    Track.db.updateMulti(Query("artist" -> _id), Query("$set" -> Query("artist" -> other)))
  }
}

object Artist extends MetaModel[Artist]("artist") {
  def findOrCreateByName(name: String): Artist = {
    db.upsert(Query("name" -> name), Query("name" -> name))
    findByName(name).get
  }

  def harmonize(name: String, rdioId: RdioId) = {
    db.upsert(Query("name" -> name), Query("$set" -> Query("name" -> name, "rdioId" -> rdioId)))
  }

  def findByRdioId(id: RdioId): Option[Artist] =
    db.findOne(Query("rdioId" -> id))

  def findByName(name: String): Option[Artist] = Artist.db.findOne(Query("name" -> name))
}