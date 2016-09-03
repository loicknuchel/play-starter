package com.flashjob.controllers.html

import com.flashjob.common.Contexts
import play.api.mvc._

case class Application(ctx: Contexts) extends Controller {
  import ctx._
  import com.flashjob.common.Contexts.ctrlToEC

  def index = Action { implicit req: Request[AnyContent] =>
    Ok(com.flashjob.views.html.index())
  }
}
