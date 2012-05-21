package net.reisub.mashio.models

case class Artist(_id: Artist.Id, name: String, rdio: RdioData[Artist], hasFakes: Boolean = false) {
  def setFakes(b: Boolean) = Artist.db.updateOne(_id, Query("$set" -> Query("hasFakes" -> b)))
}

object Artist extends MetaModel[Artist]("artist") {
  def findOrCreateByName(name: String): Artist = {
    db.upsert(
      Query("name" -> name),
      Query("$set" -> Query(
        "name"      -> name,
        "rdio.url"  -> Util.urlStr("/artist/", name),
        "rdio.key"  -> Util.keyStr("r"),
        "rdio.isFake" -> true
      )
    ))
    findByName(name).get
  }

  def harmonize(name: String, rdioId: RdioId, rdioUrl: String): Artist = {
    db.upsert(
        Query("name" -> name),
        Query("$set" -> Query(
          "name" -> name,
          "rdio.key" -> rdioId,
          "rdio.url" -> rdioUrl,
          "rdio.isFake" -> false
        )))
    findByRdioId(rdioId).get
  }

  def findByRdioId(id: RdioId): Option[Artist] =
    db.findOne(Query("rdio.key" -> id))

  def findByName(name: String): Option[Artist] = Artist.db.findOne(Query("name" -> name))
}