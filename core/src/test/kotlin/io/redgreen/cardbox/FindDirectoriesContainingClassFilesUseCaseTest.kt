package io.redgreen.cardbox

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class FindDirectoriesContainingClassFilesUseCaseTest {
  @Test
  fun `it should return class file directories from the current project`() {
    // given
    val useCase = FindDirectoriesContainingClassFilesUseCase()

    // when
    val classFileDirectoryPaths = useCase.invoke(File("."))

    // then
    assertThat(classFileDirectoryPaths)
      .containsAtLeast(
        "/build/classes/kotlin/main/io/redgreen/cardbox",
        "/build/classes/kotlin/test/io/redgreen/cardbox",
        "/build/classes/java/test",
      )
  }
}
