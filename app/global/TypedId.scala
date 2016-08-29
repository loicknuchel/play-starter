package global

import scala.util.Try

/**
 * TypedId is a helper to create specific types for IDs (based on String)
 *
 * {{{
 * case class Id(value: String) extends TypedId(value)
 * object Id extends TypedIdHelper[Id] {
 *   def generate(): Id = Id(TypedId.generate())
 *   def from(value: String): Either[String, Id] = TypedId.from(str, "JobOffer.Id").right.map(Id(_))
 * }
 * }}}
 */
class TypedId(override val underlying: String, requireTest: => Boolean = true, requireMessage: String = "") extends TypedString(underlying, requireTest, requireMessage) {
  require(TypedId.isId(underlying), s"value '$underlying' is not a correct TypedId")
}
object TypedId {
  def generate(): String = java.util.UUID.randomUUID().toString()
  def isId(value: String): Boolean = Try(java.util.UUID.fromString(value)).isSuccess
  def from(value: String, idName: String): Either[String, String] = if (isId(value)) { Right(value) } else { Left(s"Incorrect TypedId '$value' for $idName") }
}
trait TypedIdHelper[T <: TypedId] extends TypedStringHelper[T] {
  def generate(): T
}
