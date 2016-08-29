package global.models

import org.scalatestplus.play.PlaySpec

class PageSpec extends PlaySpec {
  "A Page" should {
    "have the first page at index 0" in {
      Page.prev(Page.Index(0)) mustBe None
      Page.prev(Page.Index(3)) mustBe Some(Page.Index(2))
    }
    "give next page" in {
      Page.next(Page.Index(0), Page.Size(20), Page.Count(5)) mustBe None
      Page.next(Page.Index(0), Page.Size(20), Page.Count(20)) mustBe None
      Page.next(Page.Index(0), Page.Size(20), Page.Count(21)) mustBe Some(Page.Index(1))
      Page.next(Page.Index(2), Page.Size(20), Page.Count(55)) mustBe None
      Page.next(Page.Index(2), Page.Size(20), Page.Count(60)) mustBe None
      Page.next(Page.Index(2), Page.Size(20), Page.Count(61)) mustBe Some(Page.Index(3))
      Page.next(Page.Index(2), Page.Size(20), Page.Count(100)) mustBe Some(Page.Index(3))
    }
    "calculate item indexes" in {
      Page.firstItemIndex(Page.Index(0), Page.Size(20)) mustBe 0
      Page.firstItemIndex(Page.Index(3), Page.Size(20)) mustBe 60
      Page.lastItemIndex(Page.Index(0), Page.Size(20), 20) mustBe 19
      Page.lastItemIndex(Page.Index(3), Page.Size(20), 20) mustBe 79
      Page.lastItemIndex(Page.Index(5), Page.Size(20), 5) mustBe 104
    }
    "calculate max page index" in {
      Page.maxPageIndex(Page.Size(20), Page.Count(5)) mustBe Page.Index(0)
      Page.maxPageIndex(Page.Size(20), Page.Count(20)) mustBe Page.Index(0)
      Page.maxPageIndex(Page.Size(20), Page.Count(21)) mustBe Page.Index(1)
      Page.maxPageIndex(Page.Size(20), Page.Count(63)) mustBe Page.Index(3)
      Page.maxPageIndex(Page.Size(20), Page.Count(80)) mustBe Page.Index(3)
    }
    "keep its metadata on .map method" in {
      val page = Page(List(0, 1, 2, 3, 4), Page.Index(0), Page.Size(5), Page.Count(20))
      val pageStr = page.map(_.toString)
      pageStr.index mustBe page.index
      pageStr.size mustBe page.size
      pageStr.count mustBe page.count
    }
    "not have more items than its size" in {
      a[IllegalArgumentException] should be thrownBy {
        Page(List(0, 1, 2, 3, 4, 5), Page.Index(0), Page.Size(5), Page.Count(20)) // too many items
      }
      a[IllegalArgumentException] should be thrownBy {
        Page(List(0, 1, 2, 3, 4), Page.Index(5), Page.Size(5), Page.Count(20)) // too low count
      }
    }
  }
}
