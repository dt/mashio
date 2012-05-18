package net.reisub.mashio

import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

package object models {
  type RawId = ObjectId
  type Tagged[T] = { type Tag = T }
  sealed trait RdioKey
  type RdioId = String with Tagged[RdioKey]
  type TaggedId[T] = ObjectId with Tagged[T]

  object TaggedId {
    def apply[T](id: ObjectId) = id.asInstanceOf[TaggedId[T]]
    def apply[T](id: String) = apply[T](new ObjectId(id))
    def get[T]: TaggedId[T] = apply(new ObjectId)
  }

  object RdioId {
    def apply(s: String) = s.asInstanceOf[RdioId]
  }

  object Query { def apply(clauses: (String, Any)*) = MongoDBObject.apply(clauses: _*) }
}
