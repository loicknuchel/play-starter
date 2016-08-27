package com.flashjob.domain.models

import com.flashjob.domain.models.values.Title
import global.{ TypedIdHelper, TypedId }
import play.api.libs.json.Json

case class JobOffer(
  id: JobOffer.Id,
  title: Title
)
object JobOffer {
  case class Id(_value: String) extends TypedId(_value)
  object Id extends TypedIdHelper[Id] {
    def from(str: String): Either[String, Id] = TypedId.from(str, "JobOffer.Id").right.map(Id(_))
    def generate(): Id = Id(TypedId.generate())
  }

  implicit val format = Json.format[JobOffer]
}
