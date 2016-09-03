package com.flashjob

import com.flashjob.controllers._
import com.flashjob.infrastructure.db.JobOfferRepositoryImpl
import com.flashjob.common.{ Contexts, Conf }
import infrastructure.Mongo
import play.api._
import play.api.cache.EhCacheComponents
import play.api.i18n.I18nComponents
import play.api.inject.{ SimpleInjector, NewInstanceInjector }
import play.api.libs.ws.ahc.AhcWSComponents
import play.filters.gzip.GzipFilterComponents
import play.modules.reactivemongo.{ ReactiveMongoApi, DefaultReactiveMongoApi, ReactiveMongoComponents }
import play.api.routing.Router
import router.Routes

class MyApplicationLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new MyComponents(context).application
  }
}

class MyComponents(context: ApplicationLoader.Context)
    extends BuiltInComponentsFromContext(context)
    with I18nComponents
    with AhcWSComponents
    with EhCacheComponents
    with GzipFilterComponents
    with ReactiveMongoComponents {
  val conf = Conf(configuration)
  val ctx = Contexts(actorSystem)
  val reactiveMongoApi: ReactiveMongoApi = new DefaultReactiveMongoApi(configuration, applicationLifecycle)
  val mongo = Mongo(ctx, reactiveMongoApi)
  val jobOfferRepository = JobOfferRepositoryImpl(conf, ctx, mongo)

  val router: Router = new Routes(
    httpErrorHandler,
    new html.Application(conf, ctx)(messagesApi),
    new api.Application(ctx),
    new api.JobOffers(ctx, jobOfferRepository),
    new _root_.global.controllers.Application(conf, ctx, mongo)(messagesApi),
    new _root_.controllers.Assets(httpErrorHandler)
  )

  override lazy val injector = {
    new SimpleInjector(NewInstanceInjector) +
      router +
      cookieSigner +
      csrfTokenSigner +
      httpConfiguration +
      tempFileCreator +
      global +
      crypto +
      wsApi +
      messagesApi
  }
}
object MyComponents {
  def default = new MyComponents(ApplicationLoader.createContext(Environment.simple()))
}
