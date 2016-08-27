package global

import scala.util.Try

/*
 * Ex :
 *
 * case class Id(_value: String) extends TypedId(_value = _value)
 * object Id extends TypedIdHelper[Id] {
 *   def from(str: String): Either[String, Id] = TypedId.from(str, "JobOffer.Id").right.map(Id(_))
 *   def generate(): Id = Id(TypedId.generate())
 * }
 */

class TypedId(_value: String) extends TypedString(_value) {
  require(TypedId.isId(_value), s"value '${_value}' is not a correct TypedId")
}
object TypedId {
  def generate(): String = java.util.UUID.randomUUID().toString()
  def isId(str: String): Boolean = Try(java.util.UUID.fromString(str)).toOption.isDefined
  def from(str: String, idName: String): Either[String, String] = if (isId(str)) { Right(str) } else { Left(s"Incorrect TypedId '$str' for $idName") }
}
trait TypedIdHelper[T <: TypedId] extends TypedStringHelper[T] {
  def generate(): T
}
