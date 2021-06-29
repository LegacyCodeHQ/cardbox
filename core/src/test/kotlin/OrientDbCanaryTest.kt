import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig
import com.orientechnologies.orient.core.security.OGlobalUserImpl
import com.orientechnologies.orient.server.OServer
import io.redgreen.testassist.getResourceStream
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class OrientDbCanaryTest {
  companion object {
    private lateinit var server: OServer
    private lateinit var orientDb: OrientDB

    @BeforeAll
    @JvmStatic
    fun setUp() {
      val streamConfig = getResourceStream<OrientDbCanaryTest>("config/orient-db.xml")
      server = OServer.startFromStreamConfig(streamConfig)
      server.activate()

      val defaultConfig = OrientDBConfig.defaultConfig().apply {
        users.add(OGlobalUserImpl("root", "ThisIsA_TEST", "*"))
      }

      orientDb = OrientDB("embedded:/tmp/", defaultConfig).apply {
        createIfNotExists("test", ODatabaseType.PLOCAL, defaultConfig)
      }
    }

    @AfterAll
    @JvmStatic
    fun tearDown() {
      with(orientDb) {
        drop("test")
        close()
      }
      server.shutdown()
    }
  }

  @Test
  fun `it can open a connection to the database`() {
    orientDb.open("test", "root", "ThisIsA_TEST")
  }
}
