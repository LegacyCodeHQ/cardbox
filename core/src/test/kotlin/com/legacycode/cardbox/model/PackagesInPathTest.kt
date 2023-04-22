@file:Suppress("unused")

package com.legacycode.cardbox.model

import com.google.common.truth.Truth.assertThat
import com.legacycode.cardbox.model.PackageNameResult.DefaultPackage
import com.legacycode.cardbox.model.PackageNameResult.PackageName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class PackagesInPathTest {
  companion object {
    @JvmStatic
    fun defaultPackageArtifactNames(): List<Pair<String, ArtifactName>> {
      return listOf(
        "./core/build/classes/java/test" to ArtifactName("core-java-test.jar"),
        "./core/build/classes/kotlin/test" to ArtifactName("core-kotlin-test.jar"),
        "./cli/build/classes/kotlin/test" to ArtifactName("cli-kotlin-test.jar"),
        "./core/build/classes/kotlin/main" to ArtifactName("core-kotlin-main.jar"),
        "./cli/build/classes/kotlin/main" to ArtifactName("cli-kotlin-main.jar"),
      )
    }

    @JvmStatic
    fun packageNameArtifactNames(): List<Pair<PackagesInPath, ArtifactName>> {
      val example1 = PackagesInPath(
        RelativePath("./core/build/classes/kotlin/main/io"),
        listOf(
          PackageName("io.redgreen.cardbox"),
          PackageName("io.redgreen.cardbox.model")
        )
      )

      val example2 = PackagesInPath(
        RelativePath("./cli/build/classes/kotlin/main/io"),
        listOf(
          PackageName("io.redgreen.cardbox.cli"),
          PackageName("io.redgreen.cardbox.cli.commands")
        )
      )

      return listOf(
        example1 to ArtifactName("core-kotlin-main.jar"),
        example2 to ArtifactName("cli-kotlin-main.jar"),
      )
    }
  }

  @ParameterizedTest
  @MethodSource("defaultPackageArtifactNames")
  fun `artifact name for path containing default package`(segmentArtifactName: Pair<String, ArtifactName>) {
    // given
    val (segment, artifactName) = segmentArtifactName
    val packagesInPath = PackagesInPath(RelativePath(segment), listOf(DefaultPackage))

    // when & then
    assertThat(packagesInPath.artifactName)
      .isEqualTo(artifactName)
  }

  @ParameterizedTest
  @MethodSource("packageNameArtifactNames")
  fun `artifact name for path containing non-default package`(
    packagesInPathArtifactName: Pair<PackagesInPath, ArtifactName>
  ) {
    val (packagesInPath, artifactName) = packagesInPathArtifactName

    // when & then
    assertThat(packagesInPath.artifactName)
      .isEqualTo(artifactName)
  }
}
