package com.flashjob.domain.models

import global.{ TypedIdHelper, TypedId }
import play.api.libs.json.Json

case class User(
  id: User.Id,
  name: String
)
object User {
  case class Id(value: String) extends TypedId(value)
  object Id extends TypedIdHelper[Id] {
    def from(value: String): Either[String, Id] = TypedId.from(value, "User.Id").right.map(Id(_))
    def generate(): Id = Id(TypedId.generate())
  }

  implicit val format = Json.format[User]
}
