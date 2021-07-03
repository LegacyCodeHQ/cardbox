package io.redgreen.cardbox

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.model.ClassFilesLocation
import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import io.redgreen.cardbox.model.PackageNameResult.PackageName
import io.redgreen.cardbox.model.RelativePath
import java.io.File
import org.junit.jupiter.api.Test

class ClassFilesLocationUseCaseTest {
  private val useCase = ClassFilesLocationUseCase()

  @Test
  fun `test classes package name`() {
    // given
    val testClassesPath = RelativePath("./build/classes/kotlin/test/io/redgreen/cardbox")
    val testClassesDirectory = File(testClassesPath.segment)

    // when
    val result = useCase.invoke(testClassesDirectory, testClassesPath)

    // then
    assertThat(result)
      .isEqualTo(ClassFilesLocation(testClassesPath, PackageName("io.redgreen.cardbox")))
  }

  @Test
  fun `production classes package name`() {
    // given
    val productionClassesPath = RelativePath("./build/classes/kotlin/main/io/redgreen/cardbox")
    val productionClassesDirectory = File(productionClassesPath.segment)

    // when
    val result = useCase.invoke(productionClassesDirectory, productionClassesPath)

    // then
    assertThat(result)
      .isEqualTo(
        ClassFilesLocation(productionClassesPath, PackageName("io.redgreen.cardbox"))
      )
  }

  @Test
  fun `default package`() {
    // given
    val classesPath = RelativePath("./build/classes/java/test")
    val classesDirectory = File(classesPath.segment)

    // when
    val result = useCase.invoke(classesDirectory, classesPath)

    // then
    assertThat(result)
      .isEqualTo(ClassFilesLocation(classesPath, DefaultPackage))
  }

  @Test
  fun `single identifier package name`() {
    // given
    val testClassesPath = RelativePath("./build/classes/kotlin/test/one")
    val testClassesDirectory = File(testClassesPath.segment)

    // when
    val result = useCase.invoke(testClassesDirectory, testClassesPath)

    // then
    assertThat(result)
      .isEqualTo(ClassFilesLocation(testClassesPath, PackageName("one")))
  }
}
