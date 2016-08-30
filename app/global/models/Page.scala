package global.models

import global.{ TypedIntHelper, TypedInt }
import play.api.libs.json._
import scala.concurrent.{ ExecutionContext, Future }

/**
 * Represents a Page of data.
 *
 * @tparam T the type of the elements in the page.
 * @param items the list of elements in the page.
 * @param index the number of the page (first page is 0).
 * @param size the maximum number in the page.
 * @param count the total number of elements for all pages.
 */
case class Page[+T](items: Seq[T], index: Page.Index, size: Page.Size, count: Page.Count) {
  require(items.length <= size.value, s"a page can't contain more items (${items.length}) than its size ($size)")
  require(lastItemIndex < count.value, s"the last item index ($lastItemIndex) must be strictly lower than the total count ($count)")
  lazy val prev: Option[Page.Index] = Page.prev(index)
  lazy val next: Option[Page.Index] = Page.next(index, size, count)
  lazy val indexMax: Page.Index = Page.maxPageIndex(size, count)
  lazy val firstItemIndex: Int = Page.firstItemIndex(index, size)
  lazy val lastItemIndex: Int = Page.lastItemIndex(index, size, items.length)
  def map[U](f: (T) => U): Page[U] = this.copy(items = items.map(f))
  def mapAsync[U](f: (T) => Future[U])(implicit ec: ExecutionContext): Future[Page[U]] = Future.sequence(items.map(f)).map(newItems => this.copy(items = newItems))
}
object Page {
  case class Index(value: Int) extends TypedInt(value, value >= 0, s"Index should be >= 0 ($value)") {
    def firstItem(size: Page.Size): Int = Page.firstItemIndex(this, size)
    def lastItem(size: Page.Size): Int = Page.lastItemIndex(this, size, size.value)
  }
  object Index extends TypedIntHelper[Index] {
    def from(value: Int): Either[String, Index] = Right(Index(value))
  }
  case class Size(value: Int) extends TypedInt(value, value >= 0, s"Size should be >= 0 ($value)")
  object Size extends TypedIntHelper[Size] {
    def from(value: Int): Either[String, Size] = Right(Size(value))
  }
  case class Count(value: Int) extends TypedInt(value, value >= 0, s"Count should be >= 0 ($value)")
  object Count extends TypedIntHelper[Count] {
    def from(value: Int): Either[String, Count] = Right(Count(value))
  }

  def firstItemIndex(pageIndex: Page.Index, pageSize: Page.Size): Int = pageIndex.value * pageSize.value
  def lastItemIndex(pageIndex: Page.Index, pageSize: Page.Size, nbItems: Int): Int = firstItemIndex(pageIndex, pageSize) + nbItems - 1
  def prev(pageIndex: Page.Index): Option[Page.Index] = if (pageIndex.value > 0) { Some(Page.Index(pageIndex.value - 1)) } else { None }
  def next(pageIndex: Page.Index, pageSize: Page.Size, itemCount: Page.Count): Option[Page.Index] = Option(Page.Index(pageIndex.value + 1)).filter(nextPage => firstItemIndex(nextPage, pageSize) < itemCount.value)
  def maxPageIndex(pageSize: Page.Size, itemCount: Page.Count): Page.Index = Page.Index(Math.ceil(itemCount.value.toDouble / pageSize.value.toDouble).toInt - 1)

  implicit def format[T: Format] = new Format[Page[T]] {
    val tFormatter: Format[T] = implicitly[Format[T]]
    def reads(js: JsValue): JsResult[Page[T]] = {
      JsSuccess(Page[T](
        (js \ "items").as[Seq[T]],
        (js \ "index").as[Page.Index],
        (js \ "size").as[Page.Size],
        (js \ "count").as[Page.Count]
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