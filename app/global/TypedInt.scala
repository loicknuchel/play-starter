package global

import scala.util.Try
import scala.language.implicitConversions
import play.api.data.FormError
import play.api.data.format.Formatter
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.mvc.{ QueryStringBindable, PathBindable }

/**
 * TypedInt is a helper to create types around Int
 *
 * The purpose of this class is to allow to easily transform Int into typed object to improve the type safety of your scala code.
 * It creates all the necessary bindings to work with play : router, form, json...
 *
 * {{{
 * case class Index(value: Int) extends TypedInt(value)
 * object Index extends TypedIntHelper[Index] {
 *   def from(value: Int): Either[String, Index] = Right(Index(value))
 * }
 * }}}
 */
class TypedInt(val underlying: Int, requireTest: => Boolean = true, requireMessage: String = "") {
  require(requireTest, requireMessage)
  override def toString: String = underlying.toString
}
trait TypedIntHelper[T <: TypedInt] {
  def from(value: Int): Either[String, T]
  private def fromString(str: String): Either[String, T] = Try(str.toInt).map(n => from(n)).getOrElse(Left(buildErrKey))
  protected val buildErrKey = "error.wrongFormat"
  protected val buildErrMsg = "Wrong TypedInt format"

  implicit def extract(t: T): Int = t.underlying

  // read/write value to JSON
  implicit val jsonFormat = Format(new Reads[T] {
    override def reads(json: JsValue): JsResult[T] = json.validate[Int].flatMap(value => from(value) match {
      case Right(res) => JsSuccess(res)
      case Left(err) => JsError(Seq((JsPath(List()), Seq(ValidationError(err)))))
    })
  }, new Writes[T] {
    override def writes(t: T): JsValue = JsNumber(t.underlying)
  })

  // read/write value from URL path
  implicit val pathBinder = new PathBindable[T] {
    override def bind(key: String, value: String): Either[String, T] = fromString(value)
    override def unbind(key: String, value: T): String = value.underlying.toString
  }

  // read/write value from URL query string
  implicit val queryBinder = new QueryStringBindable[T] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, T]] = params.get(key).map { values =>
      values match {
        case v :: vs => fromString(v)
        case _ => Left(buildErrMsg)
      }
    }
    override def unbind(key: String, value: T): String = value.underlying.toString
  }

  // read value from Play Form
  implicit val formMapping = new Formatter[T] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = data.get(key).map { value => fromString(value).left.map(msg => Seq(FormError(key, msg, Nil))) }.getOrElse(Left(Seq(FormError(key, buildErrKey, Nil))))
    override def unbind(key: String, value: T): Map[String, String] = Map(key -> value.underlying.toString)
  }
}
