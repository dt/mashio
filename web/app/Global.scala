import com.novus.salat._
import net.reisub.mashio.models.MetaModel
import play.api._
import play.api.Play.current

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Registering play! classloader with salat")
    val ctx = new Context {
      val name = "PlayClassloader"
      val classloaders = Seq(Play.classloader)
    }
    MetaModel.ctx = ctx
  }
}
