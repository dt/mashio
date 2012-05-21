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

import net.reisub.mashio.models._
import net.reisub.mashio.TryOpt

object Application extends Controller {
  val version = "0.1"

  def index = Action {
    Ok(views.html.index())
  }
}

object Media extends Controller {

  def art =  serve("uploads/art") _
  def track(id: String) = serve("uploads/tracks"){
      val savedPath = TryOpt(classOf[IllegalArgumentException])(TaggedId[Track](id))
        .flatMap(Track.db.findOneByID)
        .flatMap(_.path)
      savedPath match {
        case Some(p) => {
          Logger.info("serving file from path: " + p)
          p
        }
        case None => {
          Logger.warn("blindly serving file: " + id)
          id
        }
      }
    }


  def serve(path: String)(file: String): Action[AnyContent] = Action {
    Ok.sendFile(new java.io.File(path, file), inline = true)
  }
}
