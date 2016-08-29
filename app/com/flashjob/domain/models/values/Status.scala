package com.flashjob.domain.models.values

import play.api.libs.json.Json

case class Status(
  code: Int,
  message: String
)
object Status {
  implicit val format = Json.format[Status]
}
