package com.flashjob.infrastructure

import com.flashjob.common.Contexts
import com.flashjob.domain.models.values.Status
import global.GenericRepository
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.{ MultiBulkWriteResult, UpdateWriteResult, WriteResult, Command }
import reactivemongo.api.commands.bson.BSONCountCommand.{ Count, CountResult }
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import reactivemongo.api.{ BSONSerializationPack, ReadPreference }
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import scala.concurrent.Future

case class Mongo(ctx: Contexts, reactiveMongoApi: ReactiveMongoApi) {
  import ctx._
  import com.flashjob.common.Contexts.dbToEC

  def getCollection[T](name: GenericRepository.Collection[T]): MongoRepository[T] = MongoRepository[T](ctx, reactiveMongoApi, name.value)

  def ping(): Future[BSONDocument] = {
    reactiveMongoApi.database.flatMap { db =>
      val runner = Command.run(BSONSerializationPack)
      runner.apply(db, runner.rawCommand(BSONDocument("ping" -> 1))).one[BSONDocument]
    }
  }
  def status(): Future[Status] = {
    ping().map(MongoHelper.pingStatus).recover {
      case e: Throwable => Status(500, e.toString)
    }
  }
}

case class MongoRepository[T](ctx: Contexts, reactiveMongoApi: ReactiveMongoApi, collectionName: String) {
  import ctx._
  import com.flashjob.common.Contexts.dbToEC

  def jsonCollection(): Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection](collectionName))

  def find(filter: JsObject = Json.obj(), sort: JsObject = Json.obj())(implicit r: Reads[T]): Future[List[T]] =
    jsonCollection().flatMap { _.find(filter).sort(sort).cursor[T](ReadPreference.primary).collect[List]() }
  /*def findPage[T](pageIndex: Int, pageSize: Int, filter: JsObject = Json.obj(), sort: JsObject = Json.obj())(implicit r: Reads[T]): Future[Page[T]] =
    jsonCollection().flatMap { collection =>
      val bson = MongoHelper.toBson(filter)
      val command = Count(bson)
      for {
        items <- collection.find(filter).options(QueryOpts(pageIndex * pageSize, (pageIndex + 1) * pageSize - 1)).sort(sort).cursor[T](ReadPreference.primary).collect[List]()
        count <- collection.runCommand(command)
      } yield Page(items, pageIndex, pageSize, count.value)
    }*/
  def get(filter: JsObject)(implicit r: Reads[T]): Future[Option[T]] =
    jsonCollection().flatMap { _.find(filter).one[T] }
  def create(elt: T)(implicit w: OWrites[T]): Future[WriteResult] =
    jsonCollection().flatMap { _.insert(elt) }
  def update(filter: JsObject, elt: T)(implicit w: OWrites[T]): Future[UpdateWriteResult] =
    jsonCollection().flatMap { _.update(filter, elt, upsert = false, multi = false) }
  def upsert(filter: JsObject, elt: T)(implicit w: OWrites[T]): Future[UpdateWriteResult] =
    jsonCollection().flatMap { _.update(filter, elt, upsert = true, multi = false) }
  def updateAll(filter: JsObject, modifier: JsObject)(implicit w: Writes[T]): Future[UpdateWriteResult] =
    jsonCollection().flatMap { _.update(filter, modifier, upsert = false, multi = true) }
  def delete(filter: JsObject): Future[WriteResult] =
    jsonCollection().flatMap { _.remove(filter, firstMatchOnly = true) }
  def deleteAll(filter: JsObject): Future[WriteResult] =
    jsonCollection().flatMap { _.remove(filter, firstMatchOnly = false) }
  /*def count(filter: JsObject = Json.obj()): Future[Int] =
    jsonCollection().flatMap { _.runCommand(Count(MongoHelper.toBson(filter))).map(_.value) }*/
  def bulkInsert(elts: Seq[T])(implicit w: Writes[T]): Future[MultiBulkWriteResult] =
    jsonCollection().flatMap { _.bulkInsert(elts.map(e => w.writes(e).as[JsObject]).toStream, true) }
  def drop(): Future[Boolean] =
    jsonCollection().flatMap { _.drop(failIfNotFound = false) }
}

object MongoHelper {
  def toBson(json: JsValue): BSONDocument = BSONFormats.BSONDocumentFormat.reads(json).get
  def toJson(bson: BSONDocument): JsValue = Json.toJson(bson)
  def $or[T <: JsValueWrapper](field: String, values: Seq[T]): JsObject =
    Json.obj("$or" -> values.distinct.map(value => Json.obj(field -> Json.obj("$eq" -> value))))
  def pingStatus(res: BSONDocument): Status = {
    if (res.getAs[Double]("ok") == Some(1d)) {
      Status(200, "ok")
    } else {
      Status(500, "Ping answering with " + Json.stringify(Json.toJson(res)))
    }
  }
}
