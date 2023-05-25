package com.legacycode.cardbox.gradle

import com.google.common.truth.Truth.assertThat
import com.legacycode.cardbox.gradle.BuildScriptResource.KotlinDsl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubprojectDependencyTest {
  @Nested
  inner class Kotlin {
    @Test
    fun name() {
      // given
      val kotlinDsl = KotlinDsl.build("tumbleweed").content

      // when
      val dependencies = extractSubprojectDependencies(kotlinDsl)

      // then
      assertThat(dependencies).containsExactly(
          SubprojectDependency("web-server", "implementation"),
          SubprojectDependency("filesystem", "implementation"),
          SubprojectDependency("bytecode:scanner", "implementation"),
          SubprojectDependency("viz", "implementation"),
          SubprojectDependency("android", "implementation"),
          SubprojectDependency("bytecode:testing", "testImplementation"),
        ).inOrder()
    }
  }
}
