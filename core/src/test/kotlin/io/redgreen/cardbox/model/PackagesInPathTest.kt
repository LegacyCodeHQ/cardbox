@file:Suppress("unused")

package io.redgreen.cardbox.model

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class PackagesInPathTest {
  companion object {
    @JvmStatic
    fun defaultPackageArtifactNames(): List<Pair<String, String>> {
      return listOf(
        "./core/build/classes/java/test" to "core-java-test.jar",
        "./core/build/classes/kotlin/test" to "core-kotlin-test.jar",
        "./cli/build/classes/kotlin/test" to "cli-kotlin-test.jar",
        "./core/build/classes/kotlin/main" to "core-kotlin-main.jar",
        "./cli/build/classes/kotlin/main" to "cli-kotlin-main.jar",
      )
    }
  }

  @ParameterizedTest
  @MethodSource("defaultPackageArtifactNames")
  fun `artifact name for path containing default package`(segmentArtifactName: Pair<String, String>) {
    // given
    val (segment, artifactName) = segmentArtifactName
    val packagesInPath = PackagesInPath(RelativePath(segment), listOf(DefaultPackage))

    // when & then
    assertThat(packagesInPath.artifactName)
      .isEqualTo(artifactName)
  }
}
