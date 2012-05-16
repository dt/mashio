package net.reisub.mashio.models

import com.mongodb.WriteConcern

case class Artist(_id: Artist.Id, name: String) {
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

  def findByName(name: String): Option[Artist] = Artist.db.findOne(Query("name" -> name))
}