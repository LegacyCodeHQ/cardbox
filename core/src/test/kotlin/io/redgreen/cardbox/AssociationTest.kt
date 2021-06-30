package io.redgreen.cardbox

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.GuessClassFilesRootDirectoryFromPackageNameUseCase.Association
import io.redgreen.cardbox.GuessClassFilesRootDirectoryFromPackageNameUseCase.Association.SourceSet.PRODUCTION
import io.redgreen.cardbox.GuessClassFilesRootDirectoryFromPackageNameUseCase.Association.SourceSet.TEST
import io.redgreen.cardbox.GuessClassFilesRootDirectoryFromPackageNameUseCase.Association.SourceSet.UNDETERMINED
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.File
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AssociationTest {
  @Nested
  inner class JarToolPaths {
    @Test
    fun `test classes`() {
      // given
      val testClassesDirectory = File("./build/classes/kotlin/test/io/redgreen/cardbox")
      val association = Association(
        testClassesDirectory,
        PackageName("io.redgreen.cardbox")
      )

      // when & then
      assertThat(association.jarToolPath)
        .isEqualTo(File("./build/classes/kotlin/test/io"))
    }

    @Test
    fun `production classes`() {
      // given
      val productionClassesDirectory = File("./build/classes/kotlin/main/io/redgreen/cardbox")
      val association = Association(
        productionClassesDirectory,
        PackageName("io.redgreen.cardbox")
      )

      // when & then
      assertThat(association.jarToolPath)
        .isEqualTo(File("./build/classes/kotlin/main/io"))
    }

    @Test
    fun `default package`() {
      // given
      val classesDirectory = File("./build/classes/java/test")
      val association = Association(classesDirectory, DefaultPackage)

      // when & then
      assertThat(association.jarToolPath)
        .isEqualTo(File("./build/classes/java/test"))
    }
  }

  @Nested
  inner class SourceSets {
    @Test
    fun test() {
      // given
      val testClassesDirectory = File("./build/classes/kotlin/test/io/redgreen/cardbox")
      val association = Association(
        testClassesDirectory,
        PackageName("io.redgreen.cardbox")
      )

      // when & then
      assertThat(association.sourceSet)
        .isEqualTo(TEST)
    }

    @Test
    fun production() {
      val productionClassesDirectory = File("./build/classes/kotlin/main/io/redgreen/cardbox")
      val association = Association(
        productionClassesDirectory,
        PackageName("io.redgreen.cardbox")
      )

      // when & then
      assertThat(association.sourceSet)
        .isEqualTo(PRODUCTION)
    }

    @Test
    fun `default package (test)`() {
      // given
      val classesDirectory = File("./build/classes/java/test")
      val association = Association(classesDirectory, DefaultPackage)

      // when & then
      assertThat(association.sourceSet)
        .isEqualTo(TEST)
    }

    @Test
    fun `default package (production)`() {
      // given
      val classesDirectory = File("./build/classes/java/main")
      val association = Association(classesDirectory, DefaultPackage)

      // when & then
      assertThat(association.sourceSet)
        .isEqualTo(PRODUCTION)
    }

    @ParameterizedTest
    @ValueSource(
      strings = [
        "./core/build/resources/test",
        "./core/build/testData/something",
        "./core/build/io/redgreen/cardbox"
      ]
    )
    fun `classes inside undetermined locations`(path: String) {
      // given
      val classesDirectory = File(path)
      val association = Association(classesDirectory, PackageName("io.redgreen.cardbox"))

      // when & then
      assertThat(association.sourceSet)
        .isEqualTo(UNDETERMINED)
    }
  }
}
