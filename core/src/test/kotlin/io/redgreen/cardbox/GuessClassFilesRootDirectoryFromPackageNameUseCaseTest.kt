package io.redgreen.cardbox

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.model.Association
import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import io.redgreen.cardbox.model.PackageNameResult.PackageName
import java.io.File
import org.junit.jupiter.api.Test

class GuessClassFilesRootDirectoryFromPackageNameUseCaseTest {
  private val useCase = GuessClassFilesRootDirectoryFromPackageNameUseCase()

  @Test
  fun `test classes package name`() {
    // given
    val testClassesDirectory = File("./build/classes/kotlin/test/io/redgreen/cardbox")

    // when
    val result = useCase.invoke(testClassesDirectory)

    // then
    assertThat(result)
      .isEqualTo(Association(testClassesDirectory, PackageName("io.redgreen.cardbox")))
  }

  @Test
  fun `production classes package name`() {
    // given
    val productionClassesDirectory = File("./build/classes/kotlin/main/io/redgreen/cardbox")

    // when
    val result = useCase.invoke(productionClassesDirectory)

    // then
    assertThat(result)
      .isEqualTo(Association(productionClassesDirectory, PackageName("io.redgreen.cardbox")))
  }

  @Test
  fun `default package`() {
    // given
    val classesDirectory = File("./build/classes/java/test")

    // when
    val result = useCase.invoke(classesDirectory)

    // then
    assertThat(result)
      .isEqualTo(Association(classesDirectory, DefaultPackage))
  }

  @Test
  fun `single identifier package name`() {
    // given
    val testClassesDirectory = File("./build/classes/kotlin/test/one")

    // when
    val result = useCase.invoke(testClassesDirectory)

    // then
    assertThat(result)
      .isEqualTo(Association(testClassesDirectory, PackageName("one")))
  }
}
