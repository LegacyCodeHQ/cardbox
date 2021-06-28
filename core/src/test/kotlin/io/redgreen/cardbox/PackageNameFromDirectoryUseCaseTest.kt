package io.redgreen.cardbox

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.File
import org.junit.jupiter.api.Test

class PackageNameFromDirectoryUseCaseTest {
  private val useCase = PackageNameFromDirectoryUseCase()

  @Test
  fun `test classes package name`() {
    // given
    val testClassesDirectory = File("./build/classes/kotlin/test/io/redgreen/cardbox")

    // when
    val result = useCase.invoke(testClassesDirectory)

    // then
    assertThat(result)
      .isEqualTo(PackageName("io.redgreen.cardbox"))
  }

  @Test
  fun `production classes package name`() {
    // given
    val testClassesDirectory = File("./build/classes/kotlin/main/io/redgreen/cardbox")

    // when
    val result = useCase.invoke(testClassesDirectory)

    // then
    assertThat(result)
      .isEqualTo(PackageName("io.redgreen.cardbox"))
  }

  @Test
  fun `default package`() {
    // given
    val testClassesDirectory = File("./build/classes/java/test")

    // when
    val result = useCase.invoke(testClassesDirectory)

    // then
    assertThat(result)
      .isEqualTo(DefaultPackage)
  }
}
