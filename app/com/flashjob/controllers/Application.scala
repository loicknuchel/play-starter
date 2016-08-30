package com.flashjob.controllers

import com.flashjob.common.Contexts
import global.helpers.ApiHelper
import play.api.libs.json.Json
import play.api.mvc._

case class Application(ctx: Contexts) extends Controller {
  import ctx._
  import com.flashjob.common.Contexts.ctrlToEC

  def index = Action { implicit req: Request[AnyContent] =>
    ApiHelper.writeResult(Results.Ok, Json.obj(
      "resources" -> Json.arr(
        Json.obj("JobOffer" -> com.flashjob.controllers.routes.JobOffers.find().absoluteURL)
      )
    ))
  }
}
