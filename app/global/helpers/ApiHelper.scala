package global.helpers

import global.GenericRepository
import global.models.{ ApiError, Page }
import org.joda.time.DateTime
import scala.concurrent.{ ExecutionContext, Future }
import play.api.libs.json._
import play.api.mvc.BodyParsers.parse
import play.api.mvc._
import play.api.mvc.Results._
import reactivemongo.api.commands.WriteResult

object ApiHelper {

  /* Play actions */

  // TODO : must use q, sort, fields & add filter (JsValue)
  def findAction[T, Id, TNoId](repo: GenericRepository[T, Id, TNoId])(page: Page.Index, pageSize: Page.Size, q: Option[String], sort: Option[String], include: Option[String])(implicit ec: ExecutionContext, w: OWrites[T]) = Action.async { implicit req: Request[AnyContent] =>
    //val sortFields = sort.map(_.split(",").toList).getOrElse(List())
    //val includeFields = include.map(_.split(",").toList).getOrElse(List())
    ApiHelper.findPage() {
      repo.findPage(page, pageSize)
    }
  }

  def getAction[T, Id, TNoId](repo: GenericRepository[T, Id, TNoId])(id: Id)(implicit ec: ExecutionContext, w: OWrites[T]) = Action.async { implicit req: Request[AnyContent] =>
    ApiHelper.get() {
      repo.get(id)
    }
  }

  def createAction[T, Id, TNoId](repo: GenericRepository[T, Id, TNoId])(implicit ec: ExecutionContext, r: Reads[TNoId], w: OWrites[T]) = Action.async(parse.json) { implicit req: Request[JsValue] =>
    ApiHelper.create()(
      req.body.validate[TNoId],
      repo.create,
      repo.get
    )
  }

  def fullUpdateAction[T, Id, TNoId](repo: GenericRepository[T, Id, TNoId])(id: Id)(implicit ec: ExecutionContext, r: Reads[T], w: OWrites[T]) = Action.async(parse.json) { implicit req: Request[JsValue] =>
    ApiHelper.fullUpdate(id)(
      req.body.validate[T],
      repo.fullUpdate,
      repo.get
    )
  }

  // TODO : must secure update request (req.body)
  def updateAction[T, Id, TNoId](repo: GenericRepository[T, Id, TNoId])(id: Id)(implicit ec: ExecutionContext, w: OWrites[T]) = Action.async(parse.json) { implicit req: Request[JsValue] =>
    ApiHelper.update(id)(
      repo.update(id, Json.obj("$set" -> req.body)),
      repo.get
    )
  }

  def deleteAction[T, Id, TNoId](repo: GenericRepository[T, Id, TNoId])(id: Id)(implicit ec: ExecutionContext) = Action.async { implicit req: Request[AnyContent] =>
    ApiHelper.delete() {
      repo.delete(id)
    }
  }

  /* Action helpers */

  def findPage[T]()(getPage: => Future[Page[T]])(implicit ec: ExecutionContext, w: OWrites[T], req: RequestHeader): Future[Result] = {
    val start = new DateTime()
    getPage.map {
      page => resultPageSuccess(Ok, start, page)
    }.recover {
      case error: Throwable => resultError(InternalServerError, start, ApiError.from(error))
    }
  }

  def get[T]()(get: => Future[Option[T]])(implicit ec: ExecutionContext, w: OWrites[T], req: RequestHeader) = {
    returnEntity(Ok, new DateTime(), get)
  }

  def create[T, Id, TNoId]()(
    validation: JsResult[TNoId],
    create: TNoId => Future[(WriteResult, Id)],
    get: Id => Future[Option[T]]
  )(implicit ec: ExecutionContext, w: OWrites[T], req: RequestHeader): Future[Result] = {
    val start = new DateTime()
    validation match {
      case JsSuccess(data, path) =>
        create(data).flatMap {
          case (res, id) => checkResultAndReturnEntity(Created, res, start, get(id))
        }.recover {
          case error: Throwable => resultError(InternalServerError, start, ApiError.from(error))
        }
      case JsError(error) =>
        Future(resultError(BadRequest, start, ApiError.from(error)))
    }
  }

  def fullUpdate[T, Id](id: Id)(
    validation: JsResult[T],
    fullUpdate: (Id, T) => Future[WriteResult],
    get: Id => Future[Option[T]]
  )(implicit ec: ExecutionContext, w: OWrites[T], req: RequestHeader): Future[Result] = {
    val start = new DateTime()
    validation match {
      case JsSuccess(data, path) =>
        fullUpdate(id, data).flatMap {
          res => checkResultAndReturnEntity(Ok, res, start, get(id))
        }.recover {
          case error: Throwable => resultError(InternalServerError, start, ApiError.from(error))
        }
      case JsError(error) =>
        Future(resultError(BadRequest, start, ApiError.from(error)))
    }
  }

  def update[T, Id](id: Id)(
    update: => Future[WriteResult],
    get: Id => Future[Option[T]]
  )(implicit ec: ExecutionContext, w: OWrites[T], req: RequestHeader): Future[Result] = {
    val start = new DateTime()
    update.flatMap {
      res => checkResultAndReturnEntity(Ok, res, start, get(id))
    }.recover {
      case error: Throwable => resultError(InternalServerError, start, ApiError.from(error))
    }
  }

  def delete()(delete: => Future[WriteResult])(implicit ec: ExecutionContext, req: RequestHeader): Future[Result] = {
    val start = new DateTime()
    delete.map { res =>
      ApiError.from(res).map { error =>
        resultError(InternalServerError, start, error)
      }.getOrElse {
        NoContent
      }
    }
  }

  /* Helper utils */

  private def checkResultAndReturnEntity[T](successStatus: Status, res: WriteResult, start: DateTime, get: => Future[Option[T]])(implicit ec: ExecutionContext, w: OWrites[T], req: RequestHeader): Future[Result] =
    ApiError.from(res).map { error =>
      Future(resultError(InternalServerError, start, error))
    }.getOrElse {
      returnEntity(successStatus, start, get)
    }
  private def returnEntity[T](successStatus: Status, start: DateTime, get: => Future[Option[T]])(implicit ec: ExecutionContext, w: OWrites[T], req: RequestHeader) =
    get.map { dataOpt =>
      dataOpt.map { data =>
        resultSuccess(successStatus, start, data)
      }.getOrElse {
        resultError(NotFound, start, ApiError.notFound())
      }
    }.recover {
      case error: Throwable => resultError(InternalServerError, start, ApiError.from(error))
    }
  private def resultPageSuccess[T](status: Status, start: DateTime, page: Page[T])(implicit w: OWrites[T], req: RequestHeader): Result =
    writeResult(status, Json.obj(
      "data" -> page.items,
      "page" -> Json.obj(
        "index" -> page.index,
        "size" -> page.size,
        "count" -> page.count
      ),
      "metas" -> metas(start)
    ))
  private def resultSuccess[T](status: Status, start: DateTime, data: T)(implicit w: OWrites[T], req: RequestHeader): Result =
    writeResult(status, Json.obj(
      "data" -> data,
      "metas" -> metas(start)
    ))
  private def resultError(status: Status, start: DateTime, error: ApiError)(implicit req: RequestHeader): Result =
    writeResult(status, Json.obj(
      "error" -> error,
      "metas" -> metas(start)
    ))
  def metas(start: DateTime): JsObject = Json.obj(
    "exec" -> (new DateTime().getMillis - start.getMillis)
  )
  def writeResult(status: Status, data: JsObject)(implicit req: RequestHeader): Result =
    req.queryString.get("pretty").flatMap(_.find(_ == "false")).map { _ =>
      status(data)
    }.getOrElse {
      status(Json.prettyPrint(data)).as("application/json") // pretty print to be more developper friendly
    }
}
