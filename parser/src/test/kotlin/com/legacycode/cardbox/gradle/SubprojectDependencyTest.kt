package com.legacycode.cardbox.gradle

import com.google.common.truth.Truth.assertThat
import com.legacycode.cardbox.gradle.BuildScriptResource.GroovyDsl
import com.legacycode.cardbox.gradle.BuildScriptResource.KotlinDsl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubprojectDependencyTest {
  @Nested
  inner class Kotlin {
    @Test
    fun `parse a Kotlin DSL file`() {
      // given
      val kotlinDsl = KotlinDsl.build("tumbleweed").content

      // when
      val dependencies = extractSubprojectDependencies(kotlinDsl)

      // then
      assertThat(dependencies)
        .containsExactly(
          SubprojectDependency("web-server", "implementation"),
          SubprojectDependency("filesystem", "implementation"),
          SubprojectDependency("bytecode:scanner", "implementation"),
          SubprojectDependency("viz", "implementation"),
          SubprojectDependency("android", "implementation"),
          SubprojectDependency("bytecode:testing", "testImplementation"),
        )
        .inOrder()
    }
  }

  @Nested
  inner class Groovy {
    @Test
    fun `parse a Groovy DSL file`() {
      // given
      val groovyDsl = GroovyDsl.build("qr-app-signal-android").content

      // when
      val dependencies = extractSubprojectDependencies(groovyDsl)

      // then
      assertThat(dependencies)
        .containsExactly(
          SubprojectDependency("qr", "implementation"),
        )
        .inOrder()
    }
  }
}
