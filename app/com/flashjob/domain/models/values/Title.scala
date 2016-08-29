package com.flashjob.domain.models.values

import global.{ TypedStringHelper, TypedString }

case class Title(value: String) extends TypedString(value)
object Title extends TypedStringHelper[Title] {
  def from(value: String): Either[String, Title] = Right(Title(value))
}
