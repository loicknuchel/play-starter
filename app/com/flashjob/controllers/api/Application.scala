package com.flashjob.controllers.api

import com.flashjob.common.Contexts
import global.helpers.ApiHelper
import play.api.libs.json.Json
import play.api.mvc._

case class Application(ctx: Contexts) extends Controller {

  def index = Action { implicit req: Request[AnyContent] =>
    ApiHelper.writeResult(Results.Ok, Json.obj(
      "documentation" -> controllers.routes.Assets.versioned("docs/api.html").url,
      "resources" -> Json.arr(
        Json.obj("JobOffer" -> com.flashjob.controllers.api.routes.JobOffers.find().url)
      )
    ))
  }
}
