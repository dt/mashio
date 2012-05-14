package net.reisub.mashio.models

import com.mongodb.WriteConcern

case class Artist(_id: Artist.Id, name: String) {
  def mergeInto(other: Artist.Id) {
    Album.dao.updateMulti(Query("artist" -> _id), Query("$set" -> Query("artist" -> other)))
    Track.dao.updateMulti(Query("artist" -> _id), Query("$set" -> Query("artist" -> other)))
  }
}

object Artist extends MetaModel[Artist]("artist") {
  def findOrCreateByName(name: String): Artist = {
    dao.upsert(Query("name" -> name), Query("name" -> name, "fakeId" -> "fake"))
    findByName(name).get
  }

  def findByName(name: String): Option[Artist] = Artist.dao.findOne(Query("name" -> name))
}