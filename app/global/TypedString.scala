package global

import play.api.data.FormError
import play.api.data.format.Formatter
import play.api.data.validation.{ Constraint, Invalid, Valid, ValidationError }
import play.api.libs.json._
import play.api.mvc.{ PathBindable, QueryStringBindable }

/*
 * TypedString is a helper to create types around String
 *
 * The purpose of this class is to allow to easily transform String into typed object to improve the type safety of your play app via Value Classes.
 * It creates all the necessary bindings to work with router, form, json...
 *
 * Ex:
 *
 * case class Title(val _value: String) extends TypedString(_value = _value)
 * object Title extends TypedStringHelper[Title] {
 *   def from(str: String): Either[String, Title] = Right(Title(str))
 * }
 */

class TypedString(_value: String) {
  def value: String = _value
  def isEmpty: Boolean = this.value.isEmpty
  override def toString: String = this.value
}
trait TypedStringHelper[T <: TypedString] {
  def from(str: String): Either[String, T]
  protected val buildErrKey = "error.wrongFormat"
  protected val buildErrMsg = "Wrong format"

  // read/write value to JSON
  implicit val jsonFormat = Format(new Reads[T] {
    override def reads(json: JsValue): JsResult[T] = json.validate[String].flatMap(id => from(id) match {
      case Right(uuid) => JsSuccess(uuid)
      case Left(err) => JsError(Seq((JsPath(List()), Seq(ValidationError(err)))))
    })
  }, new Writes[T] {
    override def writes(t: T): JsValue = JsString(t.value)
  })

  // read/write value from URL path
  implicit val pathBinder = new PathBindable[T] {
    override def bind(key: String, value: String): Either[String, T] = from(value)
    override def unbind(key: String, value: T): String = value.value
  }

  // read/write value from URL query string
  implicit val queryBinder = new QueryStringBindable[T] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, T]] = params.get(key).map { values =>
      values match {
        case v :: vs => from(v)
        case _ => Left(buildErrMsg)
      }
    }
    override def unbind(key: String, value: T): String = value.value
  }

  // convert to a Javascript String
  /*implicit val javascriptBinder = new JavascriptLitteral[T] {
    def to(t: T): String = t.value
  }*/

  // read value from Play Form
  implicit val formMapping = new Formatter[T] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = data.get(key).map { value => from(value).left.map(msg => Seq(FormError(key, msg, Nil))) }.getOrElse(Left(Seq(FormError(key, buildErrKey, Nil))))
    override def unbind(key: String, value: T): Map[String, String] = Map(key -> value.value)
  }
}
object TypedStringConstraints {
  /*
   * cf :
   * 	- https://github.com/playframework/playframework/blob/master/framework/src/play/src/main/scala/play/api/data/validation/Validation.scala
   * 	- https://github.com/playframework/playframework/blob/master/framework/src/play/src/main/scala/play/api/data/Form.scala
   * 	- https://github.com/playframework/playframework/blob/master/framework/src/play/src/main/scala/play/api/data/Forms.scala
   */
  def nonEmpty: Constraint[TypedString] = Constraint[TypedString]("constraint.required") { o =>
    if (o == null) Invalid(ValidationError("error.required")) else if (o.value.trim.isEmpty) Invalid(ValidationError("error.required")) else Valid
  }
}