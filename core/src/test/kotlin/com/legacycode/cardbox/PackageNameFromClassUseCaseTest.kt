package com.legacycode.cardbox

import com.google.common.truth.Truth.assertThat
import com.legacycode.cardbox.model.PackageNameResult.DefaultPackage
import com.legacycode.cardbox.model.PackageNameResult.NotClassFile
import com.legacycode.cardbox.model.PackageNameResult.PackageName
import com.legacycode.testassist.getResourceStream
import org.junit.jupiter.api.Test

class PackageNameFromClassUseCaseTest {
  private val useCase = PackageNameFromClassUseCase()

  @Test
  fun `has a package name`() {
    // given
    val classFileInputStream = getResourceStream<PackageNameFromClassUseCaseTest>(
      "FindClassFileDirectoriesUseCase.class"
    )

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
