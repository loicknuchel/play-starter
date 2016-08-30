package com.flashjob.infrastructure.db

import com.flashjob.common.{ Contexts, Conf }
import com.flashjob.domain.models.{ JobOfferNoId, JobOffer }
import com.flashjob.domain.repositories.JobOfferRepository
import com.flashjob.infrastructure.{ MongoHelper, Mongo }
import global.models.Page
import play.api.libs.json.{ Json, JsObject }
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

case class JobOfferRepositoryImpl(conf: Conf, ctx: Contexts, db: Mongo) extends JobOfferRepository {
  import ctx._
  import com.flashjob.common.Contexts.dbToEC
  private val collection = db.getCollection(conf.Repositories.jobOffer)

  def find(filter: JsObject = Json.obj(), sort: JsObject = Json.obj()): Future[List[JobOffer]] = collection.find(filter, sort)
  def findByIds(ids: Seq[JobOffer.Id], sort: JsObject = Json.obj()): Future[List[JobOffer]] = collection.find(MongoHelper.$or("id", ids.map(_.value)), sort)
  def findPage(index: Page.Index, size: Page.Size, filter: JsObject = Json.obj(), sort: JsObject = Json.obj()): Future[Page[JobOffer]] = collection.findPage(index, size, filter, sort)
  def get(id: JobOffer.Id): Future[Option[JobOffer]] = collection.get(Json.obj("id" -> id))
  def create(elt: JobOfferNoId): Future[(WriteResult, JobOffer.Id)] = {
    val toCreate = JobOffer.from(elt)
    collection.create(toCreate).map { res => (res, toCreate.id) }
  }
  def fullUpdate(id: JobOffer.Id, elt: JobOffer): Future[WriteResult] = collection.fullUpdate(Json.obj("id" -> id), elt)
  def update(id: JobOffer.Id, patch: JsObject): Future[WriteResult] = collection.update(Json.obj("id" -> id), patch)
  def delete(id: JobOffer.Id): Future[WriteResult] = collection.delete(Json.obj("id" -> id))
}
