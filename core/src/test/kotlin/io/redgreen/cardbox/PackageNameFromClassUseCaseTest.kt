package io.redgreen.cardbox

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.NotClassFile
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.File
import java.io.InputStream
import org.junit.jupiter.api.Test

class PackageNameFromClassUseCaseTest {
  private val useCase = PackageNameFromClassUseCase()

  @Test
  fun `has a package name`() {
    // given
    val classFileInputStream = getResourceStream<PackageNameFromClassUseCaseTest>("FindClassFileDirectoriesUseCase.class")

    // when
    val result = useCase.invoke(classFileInputStream)

    // then
    assertThat(result)
      .isEqualTo(PackageName("io.redgreen.cardbox"))
  }

  @Test
  fun `default package`() {
    // given
    val classFileInputStream = getResourceStream<PackageNameFromClassUseCaseTest>("CanaryTest.class")

    // when
    val result = useCase.invoke(classFileInputStream)

    // then
    assertThat(result)
      .isEqualTo(DefaultPackage)
  }

  @Test
  fun `not a class file`() {
    // given
    val classFileInputStream = getResourceStream<PackageNameFromClassUseCaseTest>("ObviouslyNotClassFile")

    // when
    val result = useCase.invoke(classFileInputStream)

    // then
    assertThat(result)
      .isEqualTo(NotClassFile)
  }
}

private inline fun <reified T> getResourceStream(
  resourceFilePath: String
): InputStream {
  val separator = File.separator
  return T::class.java.getResourceAsStream("$separator$resourceFilePath")!!
}
