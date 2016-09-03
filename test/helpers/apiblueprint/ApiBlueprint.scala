package helpers.apiblueprint

import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer

// see https://apiblueprint.org/ & https://github.com/danielgtaylor/aglio

object ApiBlueprint {
  val hash = "#"
  val bullet = "+"
  val tab = "    "

  case class Document(
    metadata: Option[MetadataSection] = None,
    overview: Option[OverviewSection] = None,
    sections: List[ContentSection] = List(),
    dataStructures: List[DataStructuresSection] = List()) {
    def writes(): String = {
      metadata.map(_.writes()).getOrElse("") +
        overview.map(_.writes()).getOrElse("") +
        sections.map(_.writes()).mkString +
        dataStructures.map(_.writes()).mkString
    }
  }

  case class MetadataSection(
    host: String) {
    def writes(): String = s"FORMAT: 1A\nHOST: $host\n\n"
  }
  case class OverviewSection(
    name: String,
    description: Option[String] = None) {
    def writes(): String = {
      val descriptionF = description.map(_ + "\n\n").getOrElse("")
      s"$hash $name\n\n$descriptionF"
    }
  }
  trait ContentSection {
    def writes(nesting: String = ""): String
  }
  case class GroupSection(
    name: String,
    description: Option[String] = None,
    resources: List[ResourceSection]) extends ContentSection {
    def writes(unused: String = ""): String = {
      val descriptionF = description.map(_ + "\n\n").getOrElse("")
      s"$hash Group $name\n\n$descriptionF" + resources.map(_.writes(hash)).mkString
    }
  }
  case class ResourceSection(
    name: String,
    resource: String,
    parameters: Option[ParametersSection] = None,
    attributes: Option[AttributesSection] = None,
    model: Option[ModelSection] = None,
    actions: List[ActionSection]) extends ContentSection {
    require(actions.length > 0, s"'actions' should not be empty for ResourceSection($name)")
    def writes(nesting: String = ""): String = {
      s"$nesting$hash $name [$resource]\n\n" +
        parameters.map(_.writes()).getOrElse("") +
        attributes.map(_.writes()).getOrElse("") +
        model.map(_.writes()).getOrElse("") +
        actions.map(_.writes(nesting)).mkString
    }
  }
  case class ActionSection(
    identifier: String,
    method: String,
    resource: Option[String] = None,
    relation: Option[RelationSection] = None,
    parameters: Option[ParametersSection] = None,
    attributes: Option[AttributesSection] = None,
    requests: List[RequestSection] = List(),
    responses: List[ResponseSection] = List()) {
    require(responses.length > 0, s"'responses' should not be empty for ActionSection($identifier [$method])")
    def writes(nesting: String = ""): String = {
      val resourceF = resource.map(" " + _).getOrElse("")
      s"$nesting$hash$hash $identifier [$method$resourceF]\n\n" +
        relation.map(_.writes()).getOrElse("") +
        parameters.map(_.writes()).getOrElse("") +
        attributes.map(_.writes()).getOrElse("") +
        requests.map(_.writes()).mkString +
        responses.map(_.writes()).mkString
    }
  }
  case class ModelSection(
    mediaType: Option[String] = None,
    headers: Option[HeadersSection] = None,
    attributes: Option[AttributesSection] = None,
    body: Option[BodySection] = None,
    schema: Option[SchemaSection] = None) {
    def writes(): String = {
      val mediaTypeF = mediaType.map(" (" + _ + ")").getOrElse("")
      s"$bullet Model$mediaTypeF\n\n" +
        headers.map(_.writes(tab)).getOrElse("") +
        attributes.map(_.writes(tab)).getOrElse("") +
        body.map(_.writes(tab)).getOrElse("") +
        schema.map(_.writes(tab)).getOrElse("")
    }
  }
  case class RequestSection(
    identifier: Option[String] = None,
    mediaType: Option[String] = None,
    headers: Option[HeadersSection] = None,
    attributes: Option[AttributesSection] = None,
    body: Option[BodySection] = None,
    schema: Option[SchemaSection] = None) {
    def writes(): String = {
      val identifierF = identifier.map(" " + _).getOrElse("")
      val mediaTypeF = mediaType.map(" (" + _ + ")").getOrElse("")
      s"$bullet Request$identifierF$mediaTypeF\n\n" +
        headers.map(_.writes(tab)).getOrElse("") +
        attributes.map(_.writes(tab)).getOrElse("") +
        body.map(_.writes(tab)).getOrElse("") +
        schema.map(_.writes(tab)).getOrElse("") + "\n"
    }
  }
  case class ResponseSection(
    httpCode: Option[Int] = None,
    mediaType: Option[String] = None,
    headers: Option[HeadersSection] = None,
    attributes: Option[AttributesSection] = None,
    body: Option[BodySection] = None,
    schema: Option[SchemaSection] = None) {
    def writes(): String = {
      val httpCodeF = httpCode.map(" " + _).getOrElse("")
      val mediaTypeF = mediaType.map(" (" + _ + ")").getOrElse("")
      s"$bullet Response$httpCodeF$mediaTypeF\n\n" +
        headers.map(_.writes(tab)).getOrElse("") +
        attributes.map(_.writes(tab)).getOrElse("") +
        body.map(_.writes(tab)).getOrElse("") +
        schema.map(_.writes(tab)).getOrElse("") + "\n"
    }
  }
  case class ParametersSection(parameters: List[MSON.Property]) {
    def writes(): String = s"$bullet Parameters\n\n" + parameters.map(_.toMSON(tab)).mkString + "\n"
  }
  case class HeadersSection(headers: Map[String, String]) {
    def writes(nesting: String = ""): String = s"$nesting$bullet Headers\n\n" + headers.map { case (key, value) => s"$nesting$tab$tab$key: $value\n" } + "\n"
  }
  case class AttributesSection(properties: List[MSON.Property]) {
    def writes(nesting: String = ""): String = s"$nesting$bullet Attributes\n" + properties.map(_.toMSON(nesting + tab)) + "\n"
  }
  case class BodySection(json: JsValue) {
    def writes(nesting: String = ""): String = s"$nesting$bullet Body\n\n" + formatJson(json, nesting) + "\n"
  }
  case class SchemaSection(json: JsValue) {
    def writes(nesting: String = ""): String = s"$nesting$bullet Schema\n\n" + formatJson(json, nesting) + "\n"
  }
  case class RelationSection(identifier: String) {
    def writes(): String = s"$bullet Relation: $identifier"
  }
  case class DataStructuresSection(structures: List[MSON.Structure]) {
    def writes(): String = s"$hash Data Structures\n\n" + structures.map(_.toMSON(hash)) + "\n"
  }

  private def formatJson(json: JsValue, nesting: String): String =
    Json.prettyPrint(json).split("\n").map(nesting + tab + tab + _).mkString("\n")

  object MSON {
    case class Structure(
      name: Option[String] = None,
      parent: Option[String] = None,
      description: Option[String] = None,
      properties: List[Property]) {
      require(properties.length > 0, "'properties' should not be empty !")
      def toMSON(nesting: String = ""): String = {
        val parentF = parent.map(" (" + _ + ")").getOrElse("")
        val descriptionF = description.map(_ + "\n").getOrElse("")
        val nameF = name.map(s"$nesting$hash " + _ + s"$parentF\n$descriptionF\n").getOrElse("")
        nameF + properties.map(_.toMSON()).mkString
      }
    }
    object Structure {
      def from(name: Option[String] = None, parent: Option[String] = None, description: Option[String] = None, json: JsObject): Structure =
        Structure(name, parent, description, json.value.map { v => Property.from(v._1, v._2) }.toList)
    }
    sealed trait Property {
      def toMSON(nesting: String = ""): String
    }
    object Property {
      def from(key: String, json: JsValue): Property = json match {
        case JsNull => SimpleProperty(key)
        case JsString(value) => SimpleProperty(key, Some(value), Some("string"))
        case JsNumber(value) => SimpleProperty(key, Some(value.toString), Some("number"))
        case JsBoolean(value) => SimpleProperty(key, Some(value.toString), Some("boolean"))
        case JsObject(values) => NestedProperty(key, None, values.map { v => from(v._1, v._2) }.toList)
        case JsArray(values) => values match {
          case Nil => SimpleProperty(key, valueType = Some("array"))
          case ArrayBuffer(x: JsString, _*) => SimpleProperty(key, Some(values.take(3).map(_.as[String]).mkString(", ")), Some("array[string]"))
          case ArrayBuffer(x: JsNumber, _*) => SimpleProperty(key, Some(values.take(3).mkString(", ")), Some("array[number]"))
          case ArrayBuffer(x: JsBoolean, _*) => SimpleProperty(key, Some(values.take(3).mkString(", ")), Some("array[boolean]"))
          case ArrayBuffer(x: JsObject, _*) => NestedProperty(key, Some("array"), x.value.map { v => from(v._1, v._2) }.toList)
          // TODO case ArrayBuffer(x: JsArray, _*) =>
          case _ => SimpleProperty(key, valueType = Some("array"), description = Some("ex: " + Json.stringify(json)))
        }
        case _ => SimpleProperty(key, description = Some("ex: " + Json.stringify(json)))
      }
    }
    case class SimpleProperty(
      name: String,
      example: Option[String] = None,
      valueType: Option[String] = None,
      required: Boolean = false,
      description: Option[String] = None) extends Property {
      def toMSON(nesting: String = ""): String = {
        val exampleF = example.map(": `" + _ + "`").getOrElse("")
        val requiredF = if (required) { ", required" } else { "" }
        val valueTypeF = valueType.map(" (" + _ + s"$requiredF)").getOrElse("")
        val descriptionF = description.map(" - " + _).getOrElse("")
        s"$nesting$bullet $name$exampleF$valueTypeF$descriptionF\n"
      }
    }
    case class NestedProperty(
      name: String,
      valueType: Option[String] = None,
      properties: List[Property]) extends Property {
      require(properties.length > 0, "'properties' should not be empty !")
      def toMSON(nesting: String = ""): String = {
        val valueTypeF = valueType.map(" (" + _ + ")").getOrElse("")
        s"$nesting$bullet $name$valueTypeF\n" + properties.map(_.toMSON(nesting + tab)).mkString
      }
    }
  }
}
