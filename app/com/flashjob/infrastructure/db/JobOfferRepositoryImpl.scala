package com.flashjob.infrastructure.db

import com.flashjob.common.Conf
import com.flashjob.domain.models.JobOffer
import com.flashjob.domain.repositories.JobOfferRepository
import com.flashjob.infrastructure.Mongo
import play.api.libs.json.{ Json, JsObject }
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

case class JobOfferRepositoryImpl(conf: Conf, db: Mongo) extends JobOfferRepository {
  private val collection = db.getCollection(conf.Repositories.jobOffer)

  def find(filter: JsObject = Json.obj(), sort: JsObject = Json.obj()): Future[List[JobOffer]] = collection.find(filter, sort)
  def get(filter: JsObject): Future[Option[JobOffer]] = collection.get(filter)
  def create(elt: JobOffer): Future[WriteResult] = collection.create(elt)
  def update(filter: JsObject, elt: JobOffer): Future[WriteResult] = collection.update(filter, elt)
  def delete(filter: JsObject): Future[WriteResult] = collection.delete(filter)
}
