package com.flashjob.domain.models

import com.flashjob.domain.models.values.Title
import global.{ TypedIdHelper, TypedId }
import play.api.libs.json.Json

case class JobOffer(
  id: JobOffer.Id,
  title: Title
)
object JobOffer {
  case class Id(value: String) extends TypedId(value)
  object Id extends TypedIdHelper[Id] {
    def from(value: String): Either[String, Id] = TypedId.from(value, "JobOffer.Id").right.map(Id(_))
    def generate(): Id = Id(TypedId.generate())
  }

  def from(e: JobOfferNoId): JobOffer = JobOffer(
    JobOffer.Id.generate(),
    e.title
  )

  implicit val format = Json.format[JobOffer]
}

case class JobOfferNoId(
  title: Title
)
object JobOfferNoId {
  implicit val format = Json.format[JobOfferNoId]
}