package com.flashjob

import com.flashjob.controllers.{ JobOffers, Application }
import com.flashjob.infrastructure.db.JobOfferRepositoryImpl
import com.flashjob.common.{ Contexts, Conf }
import infrastructure.Mongo
import play.api._
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.modules.reactivemongo.{ DefaultReactiveMongoApi, ReactiveMongoComponents }
import router.Routes

class CustomApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new CustomComponents(context).application
  }
}

class CustomComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with ReactiveMongoComponents
    with I18nComponents {
  val conf = Conf(configuration)
  val ctx = Contexts(actorSystem)
  val reactiveMongoApi = new DefaultReactiveMongoApi(configuration, applicationLifecycle)
  val db = Mongo(ctx, reactiveMongoApi)
  val jobOfferRepository = JobOfferRepositoryImpl(conf, db)

  val applicationController = new Application(ctx, db)
  val jobOffersController = new JobOffers(ctx, jobOfferRepository)
  val assets = new _root_.controllers.Assets(httpErrorHandler)
  val router = new Routes(httpErrorHandler, applicationController, jobOffersController, assets)
}

/*
import play.api._
import play.api.ApplicationLoader.Context
import router.Routes

class CustomApplicationLoader extends ApplicationLoader {
  def load(context: Context) = new CustomComponents(context).application
}

import play.api.cache.EhCacheComponents

class CustomComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with EhCacheComponents
    with play.filters.gzip.GzipFilterComponents {
  import com.flashjob.common.Conf

  val conf = Conf(configuration)

  val applicationController = new controllers.Application(conf)
  val assets = new _root_.controllers.Assets(httpErrorHandler)
  //val router = new Routes(httpErrorHandler, applicationController)
}
object CustomComponents {
  def default = new CustomComponents(ApplicationLoader.createContext(Environment.simple()))
}
*/ 