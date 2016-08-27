package com.flashjob.controllers

import com.flashjob.common.Contexts
import com.flashjob.domain.repositories.JobOfferRepository
import play.api.libs.json.Json
import play.api.mvc.{ Action, Controller }

case class JobOffers(ctx: Contexts, jobOfferRepository: JobOfferRepository) extends Controller {
  import ctx._
  import com.flashjob.common.Contexts.ctrlToEC

  def list = Action.async {
    //Future(Ok(Json.obj("JobOffer" -> JobOffer(JobOffer.Id("88d27607-8f63-45e7-86d4-5205d23849fe"), Title("My first job")))))
    jobOfferRepository.find().map { jobOffers =>
      Ok(Json.obj("jobOffers" -> jobOffers))
    }
  }
}
