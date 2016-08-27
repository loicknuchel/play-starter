package com.flashjob.domain.models.values

import global.{ TypedStringHelper, TypedString }

case class Title(val _value: String) extends TypedString(_value)
object Title extends TypedStringHelper[Title] {
  def from(str: String): Either[String, Title] = Right(Title(str))
}
