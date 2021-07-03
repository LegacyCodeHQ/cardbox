package io.redgreen.cardbox

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.model.Project
import io.redgreen.cardbox.model.RelativePath
import java.io.File
import org.junit.jupiter.api.Test

class DiscoverClassFilesDirectoryPathsUseCaseTest {
  @Test
  fun `it should return class file directories from the current project`() {
    // given
    val useCase = DiscoverClassFilesDirectoryPathsUseCase()

    // when
    val classFileDirectoryPaths = useCase.invoke(Project(File(".")))

    // then
    assertThat(classFileDirectoryPaths)
      .containsAtLeast(
        RelativePath("./build/classes/kotlin/main/io/redgreen/cardbox"),
        RelativePath("./build/classes/kotlin/test/io/redgreen/cardbox"),
        RelativePath("./build/classes/java/test"),
      )
  }
}
