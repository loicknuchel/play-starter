package com.flashjob

import com.flashjob.common.{ Contexts, Conf }
import play.api._
import play.api.ApplicationLoader.Context
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
    extends BuiltInComponentsFromContext(context) {
  val conf = Conf(configuration)
  val ctx = Contexts(actorSystem)

  val applicationController = new controllers.Application(ctx)
  val assets = new _root_.controllers.Assets(httpErrorHandler)
  val router = new Routes(httpErrorHandler, applicationController, assets)
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