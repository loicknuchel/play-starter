package global.controllers

import com.flashjob.common.{ Conf, Contexts }
import com.flashjob.infrastructure.Mongo
import global.helpers.ApiHelper
import org.joda.time.DateTime
import play.api.i18n.{ Lang, MessagesApi }
import play.api.libs.json.Json
import play.api.mvc.{ AnyContent, Request, Results, Action, Controller }

case class Application(conf: Conf, ctx: Contexts, db: Mongo)(implicit messageApi: MessagesApi) extends Controller {
  import ctx._
  import com.flashjob.common.Contexts.ctrlToEC

  def status = Action.async { implicit req: Request[AnyContent] =>
    val start = new DateTime()
    for {
      dbStatus <- db.pingStatus()
    } yield {
      ApiHelper.writeResult(Results.Ok, Json.obj(
        "build" -> Json.obj(
          "date" -> global.BuildInfo.builtAtString,
          "timestamp" -> global.BuildInfo.builtAtMillis,
          "commit" -> global.BuildInfo.gitHash,
          "version" -> global.BuildInfo.version
        ),
        "checks" -> List(Json.obj(
          "name" -> "database",
          "test" -> "ping",
          "status" -> dbStatus.code,
          "message" -> dbStatus.message
        )),
        "metas" -> ApiHelper.metas(start)
      ))
    }
  }

  // TODO : don't work, can't save user preference :(
  /*def changeLang(lang: String) = Action { implicit req: Request[AnyContent] =>
    val chosen = new Lang(lang)
    messageApi.preferred(conf.App.langs.filter(_.satisfies(chosen)))
    Redirect(req.headers.get("Referer").orElse(req.headers.get("Host")).get)
  }*/
}
