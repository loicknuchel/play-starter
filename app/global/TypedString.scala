package global

import scala.language.implicitConversions
import play.api.data.FormError
import play.api.data.format.Formatter
import play.api.data.validation.{ Constraint, Invalid, Valid, ValidationError }
import play.api.libs.json._
import play.api.mvc.{ PathBindable, QueryStringBindable }

/**
 * TypedString is a helper to create types around String
 *
 * The purpose of this class is to allow to easily transform String into typed object to improve the type safety of your scala code.
 * It creates all the necessary bindings to work with play : router, form, json...
 *
 * {{{
 * case class Title(value: String) extends TypedString(value)
 * object Title extends TypedStringHelper[Title] {
 *   def from(value: String): Either[String, Title] = Right(Title(value))
 * }
 * }}}
 */
class TypedString(val underlying: String, requireTest: => Boolean = true, requireMessage: String = "") {
  require(requireTest, requireMessage)
  override def toString: String = underlying
}
trait TypedStringHelper[T <: TypedString] {
  def from(value: String): Either[String, T]
  protected val buildErrKey = "error.wrongFormat"
  protected val buildErrMsg = "Wrong TypedString format"

  implicit def extract(t: T): String = t.underlying

  // read/write value to JSON
  implicit val jsonFormat = Format(new Reads[T] {
    override def reads(json: JsValue): JsResult[T] = json.validate[String].flatMap(value => from(value) match {
      case Right(res) => JsSuccess(res)
      case Left(err) => JsError(Seq((JsPath(List()), Seq(ValidationError(err)))))
    })
  }, new Writes[T] {
    override def writes(t: T): JsValue = JsString(t.underlying)
  })

  // read/write value from URL path
  implicit val pathBinder = new PathBindable[T] {
    override def bind(key: String, value: String): Either[String, T] = from(value)
    override def unbind(key: String, value: T): String = value.underlying
  }
  implicit val pathBinderOpt = new PathBindable[Option[T]] {
    override def bind(key: String, value: String): Either[String, Option[T]] = from(value).right.map(Some(_))
    override def unbind(key: String, value: Option[T]): String = value.map(_.underlying).getOrElse("")
  }

  // read/write value from URL query string
  implicit val queryBinder = new QueryStringBindable[T] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, T]] = params.get(key).map { _.headOption.map(v => from(v)).getOrElse(Left(buildErrMsg)) }
    override def unbind(key: String, value: T): String = value.underlying
  }
  implicit val queryBinderOpt = new QueryStringBindable[Option[T]] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Option[T]]] = params.get(key).map { _.headOption.map(v => from(v).right.map(Some(_))).getOrElse(Left(buildErrMsg)) }
    override def unbind(key: String, value: Option[T]): String = value.map(_.underlying).getOrElse("")
  }

  // read value from Play Form
  implicit val formMapping = new Formatter[T] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = data.get(key).map { value => from(value).left.map(msg => Seq(FormError(key, msg, Nil))) }.getOrElse(Left(Seq(FormError(key, buildErrKey, Nil))))
    override def unbind(key: String, value: T): Map[String, String] = Map(key -> value.underlying)
  }

  // convert to a Javascript String
  /*implicit val javascriptBinder = new JavascriptLitteral[T] {
    def to(t: T): String = t.value
  }*/
}
object TypedStringConstraints {
  /*
   * cf :
   * 	- https://github.com/playframework/playframework/blob/master/framework/src/play/src/main/scala/play/api/data/validation/Validation.scala
   * 	- https://github.com/playframework/playframework/blob/master/framework/src/play/src/main/scala/play/api/data/Form.scala
   * 	- https://github.com/playframework/playframework/blob/master/framework/src/play/src/main/scala/play/api/data/Forms.scala
   */
  def nonEmpty: Constraint[TypedString] = Constraint[TypedString]("constraint.required") { o =>
    if (o == null) Invalid(ValidationError("error.required")) else if (o.underlying.trim.isEmpty) Invalid(ValidationError("error.required")) else Valid
  }
}
