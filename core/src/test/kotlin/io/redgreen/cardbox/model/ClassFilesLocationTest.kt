package io.redgreen.cardbox.model

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import io.redgreen.cardbox.model.PackageNameResult.PackageName
import io.redgreen.cardbox.model.SourceSet.PRODUCTION
import io.redgreen.cardbox.model.SourceSet.TEST
import io.redgreen.cardbox.model.SourceSet.UNDETERMINED
import java.io.File
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ClassFilesLocationTest {
  @Nested
  inner class JarToolPaths {
    @Test
    fun `test classes`() {
      // given
      val testClassesPath = RelativePath("./build/classes/kotlin/test/io/redgreen/cardbox")
      val testClassesDirectory = File("./build/classes/kotlin/test/io/redgreen/cardbox")
      val location = ClassFilesLocation(
        testClassesPath,
        testClassesDirectory,
        PackageName("io.redgreen.cardbox")
      )

      // when & then
      assertThat(location.jarToolPath)
        .isEqualTo(File("./build/classes/kotlin/test/io"))
    }

    @Test
    fun `production classes`() {
      // given
      val productionClassesPath = RelativePath("./build/classes/kotlin/main/io/redgreen/cardbox")
      val productionClassesDirectory = File("./build/classes/kotlin/main/io/redgreen/cardbox")
      val location = ClassFilesLocation(
        productionClassesPath,
        productionClassesDirectory,
        PackageName("io.redgreen.cardbox")
      )

      // when & then
      assertThat(location.jarToolPath)
        .isEqualTo(File("./build/classes/kotlin/main/io"))
    }

    @Test
    fun `default package`() {
      // given
      val classesPath = RelativePath("./build/classes/java/test")
      val classesDirectory = File("./build/classes/java/test")
      val location = ClassFilesLocation(classesPath, classesDirectory, DefaultPackage)

      // when & then
      assertThat(location.jarToolPath)
        .isEqualTo(File("./build/classes/java/test"))
    }
  }

  @Nested
  inner class SourceSets {
    @Test
    fun test() {
      // given
      val testClassesPath = RelativePath("./build/classes/kotlin/test/io/redgreen/cardbox")
      val testClassesDirectory = File("./build/classes/kotlin/test/io/redgreen/cardbox")
      val location = ClassFilesLocation(
        testClassesPath,
        testClassesDirectory,
        PackageName("io.redgreen.cardbox")
      )

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(TEST)
    }

    @Test
    fun production() {
      val productionClassesPath = RelativePath("./build/classes/kotlin/main/io/redgreen/cardbox")
      val productionClassesDirectory = File("./build/classes/kotlin/main/io/redgreen/cardbox")
      val location = ClassFilesLocation(
        productionClassesPath,
        productionClassesDirectory,
        PackageName("io.redgreen.cardbox")
      )

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(PRODUCTION)
    }

    @Test
    fun `default package (test)`() {
      // given
      val classesPath = RelativePath("./build/classes/java/test")
      val classesDirectory = File("./build/classes/java/test")
      val location = ClassFilesLocation(classesPath, classesDirectory, DefaultPackage)

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(TEST)
    }

    @Test
    fun `default package (production)`() {
      // given
      val classesPath = RelativePath("./build/classes/java/main")
      val classesDirectory = File("./build/classes/java/main")
      val location = ClassFilesLocation(classesPath, classesDirectory, DefaultPackage)

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(PRODUCTION)
    }

    @ParameterizedTest
    @ValueSource(
      strings = [
        "./core/build/resources/test",
        "./core/build/testData/something",
      ]
    )
    fun `classes inside undetermined locations`(path: String) {
      // given
      val classesPath = RelativePath(path)
      val classesDirectory = File(path)
      val location = ClassFilesLocation(classesPath, classesDirectory, PackageName("io.redgreen.cardbox"))

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(UNDETERMINED)
    }

    @Test
    fun `classes with proper package directory structure is production`() {
      val classesPath = RelativePath("./core/build/io/redgreen/cardbox")
      val classesDirectory = File("./core/build/io/redgreen/cardbox")
      val location = ClassFilesLocation(classesPath, classesDirectory, PackageName("io.redgreen.cardbox"))

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(PRODUCTION)
    }
  }
}
