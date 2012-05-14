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
  
  def serve(path: String)(file: String): Action[AnyContent] = Action { request =>
    // -- LastModified handling

    implicit val dateFormatter = {
      val formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
      formatter.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))
      formatter
    }

    val resource = new File(path + "/" + file)

    if (resource.isDirectory || !resource.getCanonicalPath.startsWith(new File(path).getCanonicalPath)) {
      NotFound
    } else if (resource.isDirectory) {
      NotFound
    } else {
      val input = new FileInputStream(resource)
      val length: Int = input.available()
	  if (length == 0) {
	    NotFound
	  } else {
        request.headers.get(IF_NONE_MATCH).filter(Some(_) == etagFor(resource)).map(_ => NotModified).getOrElse {
	      // Prepare a streamed response
	      val response = SimpleResult(
	        header = ResponseHeader(OK, Map(
	          CONTENT_LENGTH -> length.toString,
	          CONTENT_TYPE -> MimeTypes.forFileName(file).getOrElse(BINARY)
	        )),
	        Enumerator.fromStream(input)
	      )
	
	      // Add Etag if we are able to compute it
	      val taggedResponse = etagFor(resource).map(etag => response.withHeaders(ETAG -> etag)).getOrElse(response)
	
	      // Add Cache directive if configured
	      val cachedResponse = taggedResponse.withHeaders(CACHE_CONTROL -> {
	        Play.configuration.getString("\"assets.cache." + resource.getPath + "\"").getOrElse(Play.mode match {
	          case Mode.Prod => Play.configuration.getString("assets.defaultCache").getOrElse("max-age=3600")
	          case _ => "no-cache"
	        })
	      })
	      cachedResponse	
	    }
	  }
    }
  }
  
  private val lastModifieds = (new java.util.concurrent.ConcurrentHashMap[String, String]()).asScala

  private def lastModifiedFor(resource: File)(implicit dateFormatter: SimpleDateFormat): Option[String] = {
    lastModifieds.get(resource.getPath).filter(_ => Play.isProd).orElse {
      val lastModified: String = dateFormatter.format(new java.util.Date(new java.io.File(resource.getPath).lastModified))
      lastModifieds.put(resource.getPath, lastModified)
      Some(lastModified)
    }
  }

  // -- ETags handling

  private val etags = (new java.util.concurrent.ConcurrentHashMap[String, String]()).asScala

  private def etagFor(resource: File)(implicit dateFormatter: SimpleDateFormat): Option[String] = {
    etags.get(resource.getPath()).filter(_ => Play.isProd).orElse {
      val maybeEtag = lastModifiedFor(resource).map(_ + " -> " + resource.getPath).map(Codecs.sha1)
      maybeEtag.foreach(etags.put(resource.getPath, _))
      maybeEtag
    }
  }
}
