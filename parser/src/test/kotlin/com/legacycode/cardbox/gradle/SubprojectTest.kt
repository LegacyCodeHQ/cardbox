package com.legacycode.cardbox.gradle

import com.google.common.truth.Truth.assertThat
import com.legacycode.cardbox.gradle.BuildScriptResource.GroovyDsl
import com.legacycode.cardbox.gradle.BuildScriptResource.KotlinDsl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubprojectTest {
  @Nested
  inner class Kotlin {
    @Test
    fun `one include per subproject`() {
      // given
      val kotlinDsl = KotlinDsl.settings("one-include-per-subproject")

      // when
      val subprojects = extractSubprojects(kotlinDsl.content)

      // then
      assertThat(subprojects)
        .containsExactly(
          Subproject("cli", "cli"),
          Subproject("core", "core"),
          Subproject("web-server", "web-server"),
        )
        .inOrder()
    }

    @Test
    fun `one include for all subprojects`() {
      // given
      val kotlinDsl = KotlinDsl.settings("one-include-for-all-subprojects")

      // when
      val subprojects = extractSubprojects(kotlinDsl.content)

      // then
      assertThat(subprojects)
        .containsExactly(
          Subproject("core", "core"),
          Subproject("cli", "cli"),
          Subproject("parser", "parser"),
        )
        .inOrder()
    }

    @Test
    fun `one include for all subprojects, each prefixed with a colon`() {
      // given
      val kotlinDsl = KotlinDsl.settings("one-include-for-all-colon-prefix")

      // when
      val subprojects = extractSubprojects(kotlinDsl.content)

      // then
      assertThat(subprojects)
        .containsExactly(
          Subproject("core", "core"),
          Subproject("cli", "cli"),
          Subproject("parser", "parser"),
        )
        .inOrder()
    }

    @Test
    fun `one include for all with subdirectories and a trailing comma`() {
      // given
      val kotlinDsl = KotlinDsl.settings("one-include-for-all-subdirectories-trailing-comma")

      // when
      val subprojects = extractSubprojects(kotlinDsl.content)

      // then
      assertThat(subprojects)
        .containsExactly(
          Subproject("cli", "cli"),
          Subproject("bytecode:scanner", "bytecode/scanner"),
          Subproject("bytecode:samples", "bytecode/samples"),
          Subproject("web-server", "web-server"),
          Subproject("filesystem", "filesystem"),
          Subproject("vcs", "vcs"),
          Subproject("bytecode:testing", "bytecode/testing"),
          Subproject("web-client-react", "web-client-react"),
          Subproject("android", "android"),
          Subproject("viz", "viz"),
        )
        .inOrder()
    }
  }

  @Nested
  inner class Groovy {
    @Test
    fun `subproject with name change`() {
      // given
      val groovyDsl = GroovyDsl.settings("subproject-name-change-signal-android")

      // when
      val subprojects = extractSubprojects(groovyDsl.content)

      // then
      assertThat(subprojects)
        .containsExactly(
          Subproject("Signal-Android", "app"),
          Subproject("libsignal-service", "libsignal/service"),
          Subproject("lintchecks", "lintchecks"),
          Subproject("paging", "paging/lib"),
          Subproject("paging-app", "paging/app"),
          Subproject("core-util", "core-util"),
          Subproject("glide-config", "glide-config"),
          Subproject("video", "video"),
          Subproject("device-transfer", "device-transfer/lib"),
          Subproject("device-transfer-app", "device-transfer/app"),
          Subproject("image-editor", "image-editor/lib"),
          Subproject("image-editor-app", "image-editor/app"),
          Subproject("sms-exporter", "sms-exporter/lib"),
          Subproject("sms-exporter-app", "sms-exporter/app"),
          Subproject("donations", "donations/lib"),
          Subproject("donations-app", "donations/app"),
          Subproject("spinner", "spinner/lib"),
          Subproject("spinner-app", "spinner/app"),
          Subproject("contacts", "contacts/lib"),
          Subproject("contacts-app", "contacts/app"),
          Subproject("qr", "qr/lib"),
          Subproject("qr-app", "qr/app"),
          Subproject("sticky-header-grid", "sticky-header-grid"),
          Subproject("photoview", "photoview"),
          Subproject("core-ui", "core-ui"),
          Subproject("benchmark", "benchmark"),
          Subproject("microbenchmark", "microbenchmark"),
        )
        .inOrder()
    }

    @Test
    fun `basic file`() {
      // given
      val groovyDsl = GroovyDsl.settings("basic-mastodon-android")

      // when
      val subprojects = extractSubprojects(groovyDsl.content)

      // then
      assertThat(subprojects)
        .containsExactly(
          Subproject("mastodon", "mastodon"),
        )
        .inOrder()
    }
  }
}
