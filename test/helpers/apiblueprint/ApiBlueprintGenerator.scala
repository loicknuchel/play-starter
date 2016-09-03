package helpers.apiblueprint

import java.io.PrintWriter

import helpers.OneAppPerSuiteWithMyComponents
import ApiBlueprint._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{ JsString, JsObject, JsValue, Json }
import play.api.mvc.{ Result, Headers, AnyContentAsEmpty }
import play.api.test.Helpers._
import play.api.test.{ FakeHeaders, FakeRequest }
import scala.concurrent.Future
import scala.sys.process._
import scala.language.postfixOps

class ApiBlueprintGenerator extends PlaySpec with OneAppPerSuiteWithMyComponents {
  val specFile = "docs/api.apib"
  val docFile = "public/docs/api.html"
  val command = s"aglio -i $specFile -o $docFile --theme-variables streak --theme-template triple --theme-full-width"
  val host = "http://localhost:9000"
  val baseApi = "/api/v1"

  "Generator" should {
    "generate blueprint API doc" ignore {
      val metadata = MetadataSection(host + baseApi)
      val overview = OverviewSection("Flash Job", Some("Flash Job is a simple tool allowing to post Jobs for a short time. This is the documentation of its API."))

      val entryPointSection = GroupSection("Entry Point", Some("This resource does not have any attributes. Instead it offers the initial API affordances in the form of the links in the JSON body."), resources = List(
        ResourceSection("Entry Point", resource = "/", actions = List(
          actionSection("Retrieve the Entry Point", method = GET, endpoint = "")
        ))
      ))

      val jobOfferSection = groupSectionCRUD("/jobOffers", "JobOffer", "JobOffers", Some("Resources related to job offers in the API."),
        createPayload = Json.obj("title" -> "My JobOffer"),
        fullUpdateModifier = (created: JsObject) => created + ("title" -> JsString("updated title")),
        partialUpdatePayload = Json.obj("title" -> "new title"))

      val doc = Document(Some(metadata), Some(overview), sections = List(
        entryPointSection,
        jobOfferSection
      ))

      new PrintWriter(specFile) {
        write(doc.writes())
        close()
      }
      play.Logger.info("Finished")
      command !
    }
  }

  def groupSectionCRUD(endpoint: String, name: String, plural: String, description: Option[String] = None, createPayload: JsObject, fullUpdateModifier: JsObject => JsObject, partialUpdatePayload: JsObject): GroupSection = {
    val findAction = actionSection("Get " + plural, GET, endpoint, resource = Some(endpoint + "{?body}"))
    val createAction = actionSection("Create new " + name, POST, endpoint, body = Some(createPayload))
    val created = createAction.responses.headOption.flatMap(_.body).map { body => (body.json \ "data").as[JsObject] }.get
    val createdId = (created \ "id").as[String]
    val endpointDetail = endpoint + "/" + createdId
    val getAction = actionSection("Get " + name, GET, endpointDetail)
    val fullUpdateAction = actionSection("Full update a " + name, PUT, endpointDetail, body = Some(fullUpdateModifier(created)))
    val partialUpdateAction = actionSection("Partial update a " + name, PATCH, endpointDetail, body = Some(partialUpdatePayload))
    val deleteAction = actionSection("Delete a " + name, DELETE, endpointDetail)
    GroupSection(plural, description, resources = List(
      ResourceSection(name + " list", endpoint, actions = List(
        findAction,
        createAction
      )),
      ResourceSection(name, endpoint + "/{id}", parameters = Some(ParametersSection(parameters = List(
        MSON.SimpleProperty(name = "id", example = Some(createdId), valueType = Some("uuid"), required = true, description = Some(name + " ID"))
      ))), actions = List(
        getAction,
        fullUpdateAction,
        partialUpdateAction,
        deleteAction
      ))
    ))
  }
  def actionSection(name: String, method: String, endpoint: String, headers: Headers = FakeHeaders(), body: Option[JsValue] = None, resource: Option[String] = None): ActionSection = {
    val (requestContentType, response) = body.map { b => makeCall(method, endpoint, b) }.getOrElse { makeCall(method, endpoint) }
    val responseStatus = status(response)
    val responseContentType = contentType(response)
    val responseBody = responseContentType.map(_ => contentAsJson(response))
    ActionSection(
      identifier = name,
      method = method,
      resource = resource,
      requests = List(
        body.map(requestBody => RequestSection(mediaType = requestContentType, body = Some(BodySection(requestBody))))
      ).flatten,
      responses = List(
        ResponseSection(httpCode = Some(responseStatus), mediaType = responseContentType, body = responseBody.map(BodySection))
      )
    )
  }
  def makeCall(method: String, endpoint: String): (Option[String], Future[Result]) = {
    val request = FakeRequest(method, baseApi + endpoint, FakeHeaders(), AnyContentAsEmpty)
    val response = route(app, request).get
    (None, response)
  }
  def makeCall(method: String, endpoint: String, body: JsValue): (Option[String], Future[Result]) = {
    val request = FakeRequest(method, baseApi + endpoint, FakeHeaders(), body)
    val response = route(app, request).get
    (Some("application/json"), response)
  }
}
