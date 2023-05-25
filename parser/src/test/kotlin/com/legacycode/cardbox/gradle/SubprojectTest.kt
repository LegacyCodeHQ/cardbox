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
          Subproject("cli"),
          Subproject("core"),
          Subproject("web-server"),
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
          Subproject("core"),
          Subproject("cli"),
          Subproject("parser"),
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
          Subproject("core"),
          Subproject("cli"),
          Subproject("parser"),
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
          Subproject("cli"),
          Subproject("bytecode:scanner"),
          Subproject("bytecode:samples"),
          Subproject("web-server"),
          Subproject("filesystem"),
          Subproject("vcs"),
          Subproject("bytecode:testing"),
          Subproject("web-client-react"),
          Subproject("android"),
          Subproject("viz"),
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
          Subproject("Signal-Android"),
          Subproject("libsignal-service"),
          Subproject("lintchecks"),
          Subproject("paging"),
          Subproject("paging-app"),
          Subproject("core-util"),
          Subproject("glide-config"),
          Subproject("video"),
          Subproject("device-transfer"),
          Subproject("device-transfer-app"),
          Subproject("image-editor"),
          Subproject("image-editor-app"),
          Subproject("sms-exporter"),
          Subproject("sms-exporter-app"),
          Subproject("donations"),
          Subproject("donations-app"),
          Subproject("spinner"),
          Subproject("spinner-app"),
          Subproject("contacts"),
          Subproject("contacts-app"),
          Subproject("qr"),
          Subproject("qr-app"),
          Subproject("sticky-header-grid"),
          Subproject("photoview"),
          Subproject("core-ui"),
          Subproject("benchmark"),
          Subproject("microbenchmark"),
        )
        .inOrder()
    }
  }
}
