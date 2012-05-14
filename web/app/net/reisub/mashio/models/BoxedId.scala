package net.reisub.mashio.models

import play.api.mvc._


  class BoxedId[T](raw: RawId) {
    val id = TaggedId[T](raw)
    override def toString = raw.toString
  }
  
  object BoxedId {
    import org.bson.types.ObjectId.isValid
    
    implicit def boxTagged[T](id: TaggedId[T]): BoxedId[T] = new BoxedId[T](id)
    
	/**
	* QueryString binder for TaggedId
	*/
	implicit def bindableObjectIdQueryString[T] = new QueryStringBindable[BoxedId[T]] {
	  def bind(key: String, params: Map[String, Seq[String]]) = params.get(key).flatMap(_.headOption).map { value =>
	    if (isValid(value))
	      Right(new BoxedId[T](new RawId(value)))
	    else
          Left("Cannot parse parameter " + key + " as ObjectId")
	  }
	  def unbind(key: String, value: BoxedId[T]) = key + "=" + value.id.toString
	}
	
	/**
	* Path binder for ObjectId.
	*/
	implicit def bindableObjectIdPath[T] = new PathBindable[BoxedId[T]] {
	  def bind(key: String, value: String) = {
	    if (isValid(value))
	      Right(new BoxedId[T](new RawId(value)))
	    else
	      Left("Cannot parse parameter " + key + " as ObjectId")
	  }
	  def unbind(key: String, value: BoxedId[T]) = value.id.toString
	}
	
	/**
	* Convert a ObjectId to a Javascript String
	*/
	implicit def litteralTaggedId[T] = new JavascriptLitteral[BoxedId[T]] {
	  def to(value: BoxedId[T]) = value.id.toString
	}
  }