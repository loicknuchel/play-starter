package com.flashjob.infrastructure

import global.helpers.{ BaseMongoSpec, AsyncHelpers }

class MongoSpec extends BaseMongoSpec {
  "Mongo" should {
    "ping database" in {
      AsyncHelpers.whenReady(components.mongo.pingStatus()) { ping =>
        ping.code mustBe 200
      }
    }
  }
}
