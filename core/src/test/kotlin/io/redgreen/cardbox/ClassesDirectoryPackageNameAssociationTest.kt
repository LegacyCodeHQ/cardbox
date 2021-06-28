package io.redgreen.cardbox

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.GetClassesRootDirectoryUseCase.ClassesDirectoryPackageNameAssociation
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.File
import org.junit.jupiter.api.Test

class ClassesDirectoryPackageNameAssociationTest {
  @Test
  fun `test association to jar tool`() {
    // given
    val testClassesDirectory = File("./build/classes/kotlin/test/io/redgreen/cardbox")
    val association = ClassesDirectoryPackageNameAssociation(
      testClassesDirectory,
      PackageName("io.redgreen.cardbox")
    )

    // when & then
    assertThat(association.jarToolPath)
      .isEqualTo(File("./build/classes/kotlin/test/io"))
  }

  @Test
  fun `production association to jar tool path`() {
    // given
    val productionClassesDirectory = File("./build/classes/kotlin/main/io/redgreen/cardbox")
    val association = ClassesDirectoryPackageNameAssociation(
      productionClassesDirectory,
      PackageName("io.redgreen.cardbox")
    )

    // when & then
    assertThat(association.jarToolPath)
      .isEqualTo(File("./build/classes/kotlin/main/io"))
  }

  @Test
  fun `default package to jar tool path`() {
    // given
    val classesDirectory = File("./build/classes/java/test")
    val association = ClassesDirectoryPackageNameAssociation(classesDirectory, DefaultPackage)

    // when & then
    assertThat(association.jarToolPath)
      .isEqualTo(File("./build/classes/java/test"))
  }
}
