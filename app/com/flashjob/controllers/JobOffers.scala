package com.flashjob.controllers

import com.flashjob.common.Contexts
import com.flashjob.domain.models.JobOffer
import com.flashjob.domain.repositories.JobOfferRepository
import global.helpers.ApiHelper
import global.models.Page
import play.api.mvc.Controller

case class JobOffers(ctx: Contexts, jobOfferRepository: JobOfferRepository) extends Controller {
  import com.flashjob.common.Contexts.ctrlToEC
  import ctx._

  def find(page: Page.Index, pageSize: Page.Size, q: Option[String], sort: Option[String], include: Option[String]) =
    ApiHelper.findAction(jobOfferRepository)(page, pageSize, q, sort, include)
  def get(id: JobOffer.Id) = ApiHelper.getAction(jobOfferRepository)(id)
  def create = ApiHelper.createAction(jobOfferRepository)
  def fullUpdate(id: JobOffer.Id) = ApiHelper.fullUpdateAction(jobOfferRepository)(id)
  def update(id: JobOffer.Id) = ApiHelper.updateAction(jobOfferRepository)(id)
  def delete(id: JobOffer.Id) = ApiHelper.deleteAction(jobOfferRepository)(id)
}
