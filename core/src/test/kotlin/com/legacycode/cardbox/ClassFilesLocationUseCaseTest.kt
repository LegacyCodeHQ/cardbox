package com.legacycode.cardbox

import com.google.common.truth.Truth.assertThat
import com.legacycode.cardbox.model.ClassFilesLocation
import com.legacycode.cardbox.model.PackageNameResult.DefaultPackage
import com.legacycode.cardbox.model.PackageNameResult.PackageName
import com.legacycode.cardbox.model.Project
import com.legacycode.cardbox.model.RelativePath
import java.io.File
import org.junit.jupiter.api.Test

class ClassFilesLocationUseCaseTest {
  private val project = Project(File("."))
  private val useCase = ClassFilesLocationUseCase()

  @Test
  fun `test classes package name`() {
    // given
    val testClassesPath = RelativePath("./build/classes/kotlin/test/com/legacycode/cardbox")

    // when
    val result = useCase.invoke(project, testClassesPath)

    // then
    assertThat(result)
      .isEqualTo(ClassFilesLocation(testClassesPath, PackageName("com.legacycode.cardbox")))
  }

  @Test
  fun `production classes package name`() {
    // given
    val productionClassesPath = RelativePath("./build/classes/kotlin/main/com/legacycode/cardbox")

    // when
    val result = useCase.invoke(project, productionClassesPath)

    // then
    assertThat(result)
      .isEqualTo(
        ClassFilesLocation(productionClassesPath, PackageName("com.legacycode.cardbox"))
      )
  }

  @Test
  fun `default package`() {
    // given
    val classesPath = RelativePath("./build/classes/java/test")

    // when
    val result = useCase.invoke(project, classesPath)

    // then
    assertThat(result)
      .isEqualTo(ClassFilesLocation(classesPath, DefaultPackage))
  }

  @Test
  fun `single identifier package name`() {
    // given
    val testClassesPath = RelativePath("./build/classes/kotlin/test/one")

    // when
    val result = useCase.invoke(project, testClassesPath)

    // then
    assertThat(result)
      .isEqualTo(ClassFilesLocation(testClassesPath, PackageName("one")))
  }
}
