package controllers

import play.api._
import play.api.mvc._
import play.api.libs._
import play.api.libs.iteratee._
import Play.current
import java.io._
import scalax.io.{ Resource }
import java.text.SimpleDateFormat
import collection.JavaConverters._


object Application extends Controller {
  val version = "0.1"

  def index = Action {
    Ok(views.html.index())
  }
}

object Media extends Controller {

  def art = serve("uploads/art") _
  def track = serve("uploads/tracks") _

  def serve(path: String)(file: String): Action[AnyContent] = Action {
    Ok.sendFile(new java.io.File(path, file))
  }
}
