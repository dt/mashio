package net.reisub.mashio.models

import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.Imports._
import com.mongodb.WriteConcern
import com.novus.salat.dao.{ModelCompanion, SalatDAO}

abstract class MetaModel[T <: AnyRef : Manifest](val collectionName: String) {
  type Id = TaggedId[T]
  type RdioId = RdioKey[T]
  def newId = TaggedId.get[T]

  object db extends ModelCompanion[T, Id] {
    implicit var ctx = MetaModel.ctx
    val collection = MongoConnection()("mashio")(collectionName)
    override val dao =  new SalatDAO[T, Id](collection = collection){}
    def upsert(q: DBObject, newObj: DBObject) = update(q, newObj, upsert = true, multi = false, wc = WriteConcern.SAFE)
    def updateMulti(q: DBObject, newObj: DBObject) = update(q, newObj, upsert = false, multi = true, wc = WriteConcern.SAFE)
    def updateOne(id: Id, newObj: DBObject) = update(Query("_id" -> id), newObj, upsert = false, multi = false, wc = WriteConcern.SAFE)
    def findAll(ids: Iterable[Id]): Iterator[T] = find(Query("_id" -> Query("$in" -> ids)))
  }

  def clearAll() = db.remove(Query())

  def reload(id: Id): T = db.findOneByID(id).get

  object Util {
    private val reg = "[^A-Za-z0-9_]".r
    def urlStr(prefix: String, name: String) = prefix + reg.replaceAllIn(name.replace(" ", "_"), "") + "/"
    def keyStr[T](pre: String) = RdioId[T](pre + "0" + (new ObjectId).toString)

  }
}

object MetaModel {
  def ALL = Set(Artist, Album, Track)
  var ctx = com.novus.salat.global.ctx
}