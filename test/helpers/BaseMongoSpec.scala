package helpers

import com.github.simplyscala.{ MongoEmbedDatabase, MongodProps }
import de.flapdoodle.embed.mongo.distribution.Version
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

/*
  See :
    - https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo
    - https://github.com/SimplyScala/scalatest-embedmongo
    - http://geyfman.net/post/60284232731/embedded-mongo-and-play-framework-for-better
 */
abstract class BaseMongoSpec extends PlaySpec with MongoEmbedDatabase with BeforeAndAfterAll with OneAppPerSuiteWithMyComponents {
  // TODO : how to configure reactivemongo to load embed mongo ????, will not work if mongo is already started (same port)
  private val mongoPort = 27017
  private var mongoProps: MongodProps = null

  override def beforeAll() = {
    mongoProps = mongoStart(port = mongoPort, version = Version.V2_7_1)
    super.beforeAll()
  }

  override def afterAll() = {
    mongoStop(mongoProps)
    super.afterAll()
  }
}
