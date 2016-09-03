package com.flashjob.controllers.html

import com.flashjob.common.{ Conf, Contexts }
import play.api.i18n.MessagesApi
import play.api.mvc._

case class Application(conf: Conf, ctx: Contexts)(implicit messageApi: MessagesApi) extends Controller {
  implicit val langs = conf.App.langs
  import ctx._
  import com.flashjob.common.Contexts.ctrlToEC

  def index = Action { implicit req: Request[AnyContent] =>
    Ok(com.flashjob.views.html.index())
  }
}
