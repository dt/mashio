package net.reisub.mashio
import play.api.libs.json._

class RichJsObject(obj: JsObject) {
  def /(fieldName: String): JsSetter = new JsSetter {
    override def setTo(value: JsValue) =
      Some(JsObject(obj.fields.filterNot(_._1 == fieldName) ++ List(fieldName -> value)))

    override def /(innerName: String) = obj.value.get(fieldName)
      .flatMap(_.asOpt[JsObject])
      .map(new JsSetterImpl(setTo _, _, innerName))
      .getOrElse(JsSetter.Noop)
  }
}

object RichJsObject {
  object Implicits {
    implicit def enrichJsObject(jsObject: JsObject): RichJsObject = new RichJsObject(jsObject)
  }
}

trait JsSetter {
  def /(fieldName: String): JsSetter
  def setTo(value: JsValue): Option[JsObject]
}

object JsSetter {
  object Noop extends JsSetter {
    override def /(fieldName: String) = this
    override def setTo(value: JsValue) = None
  }
}

class JsSetterImpl(setter: JsValue => Option[JsObject], old: JsObject, outerField: String) extends JsSetter {
  def /(fieldName: String) = old.value.get(outerField).flatMap(_.asOpt[JsObject])
      .map(par => new JsSetterImpl(setTo _, par, fieldName)).getOrElse(JsSetter.Noop)

  def setTo(value: JsValue) =
    setter(JsObject(old.fields.filterNot(_._1 == outerField) ++ List(outerField -> value)))
}