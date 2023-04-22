package com.legacycode.cardbox.model

import com.google.common.truth.Truth.assertThat
import com.legacycode.cardbox.model.PackageNameResult.DefaultPackage
import com.legacycode.cardbox.model.PackageNameResult.PackageName
import com.legacycode.cardbox.model.SourceSet.PRODUCTION
import com.legacycode.cardbox.model.SourceSet.TEST
import com.legacycode.cardbox.model.SourceSet.UNDETERMINED
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
      val testClassesPath = RelativePath("./build/classes/kotlin/test/com/legacycode/cardbox")
      val location = ClassFilesLocation(
        testClassesPath,
        PackageName("com.legacycode.cardbox")
      )

      // when & then
      assertThat(location.pathForJarTool)
        .isEqualTo(File("./build/classes/kotlin/test/com"))
    }

    @Test
    fun `production classes`() {
      // given
      val productionClassesPath = RelativePath("./build/classes/kotlin/main/com/legacycode/cardbox")
      val location = ClassFilesLocation(
        productionClassesPath,
        PackageName("com.legacycode.cardbox")
      )

      // when & then
      assertThat(location.pathForJarTool)
        .isEqualTo(File("./build/classes/kotlin/main/com"))
    }

    @Test
    fun `default package`() {
      // given
      val classesPath = RelativePath("./build/classes/java/test")
      val location = ClassFilesLocation(classesPath, DefaultPackage)

      // when & then
      assertThat(location.pathForJarTool)
        .isEqualTo(File("./build/classes/java/test"))
    }
  }

  @Nested
  inner class SourceSets {
    @Test
    fun test() {
      // given
      val testClassesPath = RelativePath("./build/classes/kotlin/test/com/legacycode/cardbox")
      val location = ClassFilesLocation(
        testClassesPath,
        PackageName("com.legacycode.cardbox")
      )

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(TEST)
    }

    @Test
    fun production() {
      val productionClassesPath = RelativePath("./build/classes/kotlin/main/com/legacycode/cardbox")
      val location = ClassFilesLocation(
        productionClassesPath,
        PackageName("com.legacycode.cardbox")
      )

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(PRODUCTION)
    }

    @Test
    fun `default package (test)`() {
      // given
      val classesPath = RelativePath("./build/classes/java/test")
      val location = ClassFilesLocation(classesPath, DefaultPackage)

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(TEST)
    }

    @Test
    fun `default package (production)`() {
      // given
      val classesPath = RelativePath("./build/classes/java/main")
      val location = ClassFilesLocation(classesPath, DefaultPackage)

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
      val location = ClassFilesLocation(classesPath, PackageName("com.legacycode.cardbox"))

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(UNDETERMINED)
    }

    @Test
    fun `classes with proper package directory structure is production`() {
      val classesPath = RelativePath("./core/build/com/legacycode/cardbox")
      val location = ClassFilesLocation(classesPath, PackageName("com.legacycode.cardbox"))

      // when & then
      assertThat(location.sourceSet)
        .isEqualTo(PRODUCTION)
    }
  }
}
