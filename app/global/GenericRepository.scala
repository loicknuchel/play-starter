package global

import play.api.libs.json.{ Json, JsObject }
import reactivemongo.api.commands.WriteResult
import scala.concurrent.Future

trait GenericRepository[T] {
  def find(filter: JsObject = Json.obj(), sort: JsObject = Json.obj()): Future[List[T]]
  def get(filter: JsObject): Future[Option[T]]
  def create(elt: T): Future[WriteResult]
  def update(filter: JsObject, elt: T): Future[WriteResult]
  def delete(filter: JsObject): Future[WriteResult]
}
object GenericRepository {
  case class Collection[T](val value: String)
}
