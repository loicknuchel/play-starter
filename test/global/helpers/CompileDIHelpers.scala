package global.helpers

import com.flashjob.MyComponents
import org.scalatest.{ TestData, Suite }
import org.scalatestplus.play.{ OneServerPerTest, OneAppPerSuite, OneAppPerTest, OneServerPerSuite }
import play.api._

/** From https://github.com/playframework/play-scala-compile-di-with-tests */

trait WithApplicationComponents[T <: BuiltInComponents] {
  private var _components: T = _

  // accessed to get the components in tests
  final def components: T = _components

  // overridden by subclasses
  def createComponents(context: ApplicationLoader.Context): T

  // creates a new application and sets the components
  def newApplication: Application = {
    _components = createComponents(context)
    _components.application
  }

  def context: ApplicationLoader.Context = {
    val classLoader = ApplicationLoader.getClass.getClassLoader
    val env = new Environment(new java.io.File("."), classLoader, Mode.Test)
    ApplicationLoader.createContext(env)
  }
}

/* generic traits */

trait OneAppPerTestWithComponents[T <: BuiltInComponents] extends OneAppPerTest with WithApplicationComponents[T] {
  this: Suite =>
  override def newAppForTest(testData: TestData): Application = newApplication
}

trait OneAppPerSuiteWithComponents[T <: BuiltInComponents] extends OneAppPerSuite with WithApplicationComponents[T] {
  this: Suite =>
  override implicit lazy val app: Application = newApplication
}

trait OneServerPerTestWithComponents[T <: BuiltInComponents] extends OneServerPerTest with WithApplicationComponents[T] {
  this: Suite =>
  override def newAppForTest(testData: TestData): Application = newApplication
}

trait OneServerPerSuiteWithComponents[T <: BuiltInComponents] extends OneServerPerSuite with WithApplicationComponents[T] {
  this: Suite =>
  override implicit lazy val app: Application = newApplication
}

/* traits with MyComponents */

trait OneAppPerTestWithMyComponents extends OneAppPerTestWithComponents[MyComponents] {
  this: Suite =>
  override def createComponents(context: ApplicationLoader.Context): MyComponents = new MyComponents(context)
}

trait OneAppPerSuiteWithMyComponents extends OneAppPerSuiteWithComponents[MyComponents] {
  this: Suite =>
  override def createComponents(context: ApplicationLoader.Context): MyComponents = new MyComponents(context)
}

trait OneServerPerTestWithMyComponents extends OneServerPerTestWithComponents[MyComponents] {
  this: Suite =>
  override def createComponents(context: ApplicationLoader.Context): MyComponents = new MyComponents(context)
}

trait OneServerPerSuiteWithMyComponents extends OneServerPerSuiteWithComponents[MyComponents] {
  this: Suite =>
  override def createComponents(context: ApplicationLoader.Context): MyComponents = new MyComponents(context)
}
