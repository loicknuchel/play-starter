package global.controllers

import global.helpers.OneAppPerSuiteWithMyComponents
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsValue
import play.api.test._
import play.api.test.Helpers._

class ApplicationSpec extends PlaySpec with OneAppPerSuiteWithMyComponents {
  "Application" should {
    "return app status on /status" in {
      val response = route(app, FakeRequest(GET, "/status")).get
      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      (contentAsJson(response) \ "build" \ "date").asOpt[String].isDefined mustBe true
      (contentAsJson(response) \ "build" \ "commit").asOpt[String].isDefined mustBe true
      (contentAsJson(response) \ "checks").asOpt[List[JsValue]].isDefined mustBe true
    }
  }
}
