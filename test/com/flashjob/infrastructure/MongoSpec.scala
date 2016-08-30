package com.flashjob.infrastructure

import helpers.{ OneAppPerSuiteWithMyComponents, BaseMongoSpec, AsyncHelpers }
import org.scalatestplus.play.PlaySpec

// class MongoSpec extends BaseMongoSpec { // TODO : not used until I can configure EmbedMongo on a specific port
class MongoSpec extends PlaySpec with OneAppPerSuiteWithMyComponents {
  "Mongo" should {
    "ping database" in {
      AsyncHelpers.whenReady(components.mongo.pingStatus()) { ping =>
        ping.code mustBe 200
      }
    }
  }
}
