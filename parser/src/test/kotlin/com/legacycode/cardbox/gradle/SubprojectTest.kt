package com.legacycode.cardbox.gradle

import com.google.common.truth.Truth.assertThat
import com.legacycode.cardbox.gradle.SettingsResource.GroovyDsl
import com.legacycode.cardbox.gradle.SettingsResource.KotlinDsl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubprojectTest {
  @Nested
  inner class KotlinDsl {
    @Test
    fun `one include per subproject`() {
      // given
      val kotlinDsl = KotlinDsl("one-include-per-subproject")

      // when
      val subprojects = extractSubprojects(kotlinDsl.content)

      // then
      assertThat(subprojects)
        .containsExactly(
          "cli",
          "core",
          "web-server",
        )
        .inOrder()
    }

    @Test
    fun `one include for all subprojects`() {
      // given
      val kotlinDsl = KotlinDsl("one-include-for-all-subprojects")

      // when
      val subprojects = extractSubprojects(kotlinDsl.content)

      // then
      assertThat(subprojects)
        .containsExactly(
          "core",
          "cli",
          "parser",
        )
        .inOrder()
    }

    @Test
    fun `one include for all subprojects, each prefixed with a colon`() {
      // given
      val kotlinDsl = KotlinDsl("one-include-for-all-colon-prefix")

      // when
      val subprojects = extractSubprojects(kotlinDsl.content)

      // then
      assertThat(subprojects)
        .containsExactly(
          "core",
          "cli",
          "parser",
        )
        .inOrder()
    }

    @Test
    fun `one include for all with subdirectories and a trailing comma`() {
      // given
      val kotlinDsl = KotlinDsl("one-include-for-all-subdirectories-trailing-comma")

      // when
      val subprojects = extractSubprojects(kotlinDsl.content)

      // then
      assertThat(subprojects)
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
    fun `subproject with name change`() {
      // given
      val groovyDsl = GroovyDsl("subproject-name-change-signal-android")

      // when
      val subprojects = extractSubprojects(groovyDsl.content)

      // then
      assertThat(subprojects)
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
