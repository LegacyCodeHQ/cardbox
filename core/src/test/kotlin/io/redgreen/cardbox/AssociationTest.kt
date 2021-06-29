package io.redgreen.cardbox

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.GetClassesRootDirectoryUseCase.Association
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.File
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
}
