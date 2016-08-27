package com.flashjob.domain.models

import global.{ TypedIdHelper, TypedId }
import play.api.libs.json.Json

case class User(
  id: User.Id,
  name: String
)
object User {
  case class Id(_value: String) extends TypedId(_value)
  object Id extends TypedIdHelper[Id] {
    def from(str: String): Either[String, Id] = TypedId.from(str, "User.Id").right.map(Id(_))
    def generate(): Id = Id(TypedId.generate())
  }

  implicit val format = Json.format[User]
}
