package global.models

import play.api.libs.json._
import scala.concurrent.{ ExecutionContext, Future }

/**
 * Represents a Page of data.
 *
 * @tparam T the type of the elements in the page.
 *
 * @param items the list of elements in the page.
 * @param index the number of the page (first page is 0).
 * @param size the maximum number in the page.
 * @param count the total number of elements for all pages.
 */
case class Page[+T](items: Seq[T], index: Int, size: Int, count: Long) {
  lazy val prev: Option[Int] = Page.prev(index)
  lazy val next: Option[Int] = Page.next(index, size, count)
  lazy val indexMax: Int = Page.maxPageIndex(size, count)
  lazy val startItemIndex: Int = Page.startItemIndex(index, size)
  lazy val endItemIndex: Int = Page.endItemIndex(index, size, items.length)
  def map[U](f: (T) => U): Page[U] = this.copy(items = items.map(f))
  def mapAsync[U](f: (T) => Future[U])(implicit ec: ExecutionContext): Future[Page[U]] = Future.sequence(items.map(f)).map(newItems => this.copy(items = newItems))
}
object Page {
  val defaultSize: Int = 20
  def startItemIndex(pageIndex: Int, pageSize: Int): Int = pageIndex * pageSize
  def endItemIndex(pageIndex: Int, pageSize: Int, nbItems: Int): Int = startItemIndex(pageIndex, pageSize) + nbItems - 1
  def prev(pageIndex: Int): Option[Int] = Option(pageIndex - 1).filter(_ >= 0)
  def next(pageIndex: Int, pageSize: Int, itemCount: Long): Option[Int] = Option(pageIndex + 1).filter(nextPage => startItemIndex(nextPage, pageSize) < itemCount)
  def maxPageIndex(pageSize: Int, itemCount: Long): Int = Math.ceil(itemCount.toDouble / pageSize.toDouble).toInt

  implicit def format[T: Format] = new Format[Page[T]] {
    val tFormatter: Format[T] = implicitly[Format[T]]
    def reads(js: JsValue): JsResult[Page[T]] = {
      JsSuccess(Page[T](
        (js \ "items").as[Seq[T]],
        (js \ "index").as[Int],
        (js \ "size").as[Int],
        (js \ "count").as[Long]
      ))
    }
    def writes(p: Page[T]): JsValue = {
      Json.obj(
        "items" -> p.items,
        "index" -> p.index,
        "size" -> p.size,
        "count" -> p.count
      )
    }
  }
}