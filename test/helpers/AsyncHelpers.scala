package helpers

import scala.concurrent.duration.{ Duration, DurationInt }
import scala.concurrent.{ Await, Future }
import scala.language.postfixOps
import scala.util.{ Failure, Success, Try }

object AsyncHelpers {
  def whenReady[T](result: Future[T], timeout: Duration = 10 second)(expectation: T => Unit) = {
    expectation(Await.result(result, timeout))
  }

  def tryWhenReady[T](result: Future[Try[T]], timeout: Duration = 10 second)(failure: Throwable => Unit)(expectation: T => Unit) = {
    Await.result(result, timeout) match {
      case Failure(e) => failure(e)
      case Success(result) => expectation(result)
    }
  }
}
