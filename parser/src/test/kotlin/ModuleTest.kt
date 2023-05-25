import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ModuleTest {
  @Nested
  inner class KotlinDsl {
    @Test
    fun `extract module names`() {
      // given
      val ktsResource = SettingsKtsResource("include-params-single-line")

      // when
      val modules = extractModules(ktsResource.content)

      // then
      assertThat(modules)
        .containsExactly(
          "cli",
          "core",
          "web-server",
        )
        .inOrder()
    }

    @Test
    fun `sample 2`() {
      // given
      val settingsContent = """
      rootProject.name = "cardbox"
      include(
        "core",
        "cli",
        "parser"
      )
    """.trimIndent()

      // when
      val modules = extractModules(settingsContent)

      // then
      assertThat(modules)
        .containsExactly(
          "core",
          "cli",
          "parser",
        )
        .inOrder()
    }

    @Test
    fun `sample 3`() {
      // given
      val settingsContent = """
      rootProject.name = "cardbox"
      include(
        ":core",
        ":cli",
        ":parser"
      )
    """.trimIndent()

      // when
      val modules = extractModules(settingsContent)

      // then
      assertThat(modules)
        .containsExactly(
          "core",
          "cli",
          "parser",
        )
        .inOrder()
    }

    @Test
    fun `sample 4`() {
      val settingsContent = """
      rootProject.name = "tumbleweed"
      include(
        ":cli",
        ":bytecode:scanner",
        ":bytecode:samples",
        ":web-server",
        ":filesystem",
        ":vcs",
        ":bytecode:testing",
        ":web-client-react",
        ":android",
        ":viz",
      )
    """.trimIndent()

      // when
      val modules = extractModules(settingsContent)

      // then
      assertThat(modules)
        .containsExactly(
          "cli",
          "bytecode:scanner",
          "bytecode:samples",
          "web-server",
          "filesystem",
          "vcs",
          "bytecode:testing",
          "web-client-react",
          "android",
          "viz",
        )
        .inOrder()
    }
  }

  @Nested
  inner class GroovyDsl {
    @Test
    fun `sample 5`() {
      val settingsContent = """
      pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            url "https://raw.githubusercontent.com/signalapp/maven/master/sqlcipher/release/"
            content {
                includeGroupByRegex "org\\.signal.*"
            }
        }
        maven {
            url "https://dl.cloudsmith.io/qxAgwaeEE1vN8aLU/mobilecoin/mobilecoin/maven/"
        }
        jcenter {
            content {
                includeVersion "mobi.upod", "time-duration-picker", "1.1.3"
            }
        }
    }
}

enableFeaturePreview('VERSION_CATALOGS')

include ':app'
include ':libsignal-service'
include ':lintchecks'
include ':paging'
include ':paging-app'
include ':core-util'
include ':glide-config'
include ':video'
include ':device-transfer'
include ':device-transfer-app'
include ':image-editor'
include ':image-editor-app'
include ':sms-exporter'
include ':sms-exporter-app'
include ':donations'
include ':donations-app'
include ':spinner'
include ':spinner-app'
include ':contacts'
include ':contacts-app'
include ':qr'
include ':qr-app'
include ':sticky-header-grid'
include ':photoview'
include ':core-ui'
include ':benchmark'

project(':app').name = 'Signal-Android'
project(':paging').projectDir = file('paging/lib')
project(':paging-app').projectDir = file('paging/app')

project(':device-transfer').projectDir = file('device-transfer/lib')
project(':device-transfer-app').projectDir = file('device-transfer/app')

project(':libsignal-service').projectDir = file('libsignal/service')

project(':image-editor').projectDir = file('image-editor/lib')
project(':image-editor-app').projectDir = file('image-editor/app')

project(':sms-exporter').projectDir = file('sms-exporter/lib')
project(':sms-exporter-app').projectDir = file('sms-exporter/app')

project(':donations').projectDir = file('donations/lib')
project(':donations-app').projectDir = file('donations/app')

project(':spinner').projectDir = file('spinner/lib')
project(':spinner-app').projectDir = file('spinner/app')

project(':contacts').projectDir = file('contacts/lib')
project(':contacts-app').projectDir = file('contacts/app')

project(':qr').projectDir = file('qr/lib')
project(':qr-app').projectDir = file('qr/app')

rootProject.name='Signal'

apply from: 'dependencies.gradle'
include ':microbenchmark'
""".trimIndent()

      // when
      val modules = extractModules(settingsContent)

      // then
      assertThat(modules)
        .containsExactly(
          "Signal-Android",
          "libsignal-service",
          "lintchecks",
          "paging",
          "paging-app",
          "core-util",
          "glide-config",
          "video",
          "device-transfer",
          "device-transfer-app",
          "image-editor",
          "image-editor-app",
          "sms-exporter",
          "sms-exporter-app",
          "donations",
          "donations-app",
          "spinner",
          "spinner-app",
          "contacts",
          "contacts-app",
          "qr",
          "qr-app",
          "sticky-header-grid",
          "photoview",
          "core-ui",
          "benchmark",
          "microbenchmark",
        )
        .inOrder()
    }
  }
}
