package com.flashjob.controllers

import com.flashjob.common.Contexts
import com.flashjob.domain.models.JobOffer
import com.flashjob.domain.models.values.Title
import play.api.libs.json.Json
import play.api.mvc.{ Action, Controller }

import scala.concurrent.Future

case class Application(ctx: Contexts) extends Controller {
  import ctx._
  import com.flashjob.common.Contexts.ctrlToEC

  def index = Action {
    Ok("index")
  }

  def test = Action.async {
    Future(Ok(Json.obj("JobOffer" -> JobOffer(JobOffer.Id("88d27607-8f63-45e7-86d4-5205d23849fe"), Title("My first job")))))
  }

  def status = Action {
    Ok(Json.obj(
      "build" -> Json.obj(
        "date" -> global.BuildInfo.builtAtString,
        "timestamp" -> global.BuildInfo.builtAtMillis,
        "commit" -> global.BuildInfo.gitHash,
        "version" -> global.BuildInfo.version
      ),
      "checks" -> List(Json.obj(
        "name" -> "demopsug (Postgresql)",
        "test" -> "SELECT 1",
        "status" -> 200,
        "message" -> ""
      ))
    ))
  }
}
