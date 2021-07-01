package io.redgreen.cardbox.model

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.model.PackageNameResult.PackageName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class JarToolShellCommandTest {
  @Nested
  inner class ToString {
    @Test
    fun `command for multiple packages (homogeneous)`() {
      // given
      val packagesInPath = listOf(
        PackagesInPath(
          RelativePath("./core/build/classes/kotlin/main/io"),
          listOf(PackageName("io.redgreen.cardbox"))
        ),

        PackagesInPath(
          RelativePath("./core/build/classes/kotlin/main/io"),
          listOf(PackageName("io.redgreen.cardbox.model"))
        ),
      )

      // when
      val shellCommand = JarToolShellCommand.from(ArtifactName("core-kotlin-main.jar"), packagesInPath)

      // then
      assertThat(shellCommand.toString())
        .isEqualTo("jar -c --file core-kotlin-main.jar -C ./core/build/classes/kotlin/main/ io/")
    }

    @Test
    fun `command for multiple packages (heterogeneous)`() {
      // given
      val packagesInPath = listOf(
        PackagesInPath(
          RelativePath("./core/build/classes/kotlin/main/io"),
          listOf(PackageName("io.redgreen.cardbox"))
        ),

        PackagesInPath(
          RelativePath("./core/build/classes/kotlin/main/org"),
          listOf(PackageName("org.asm.java.bytecode"))
        ),
      )

      // when
      val shellCommand = JarToolShellCommand.from(ArtifactName("core-kotlin-main.jar"), packagesInPath)

      // then
      assertThat(shellCommand.toString())
        .isEqualTo("jar -c --file core-kotlin-main.jar -C ./core/build/classes/kotlin/main/ io/ org/")
    }
  }

  @Nested
  inner class ProcessBuilder {
    // given
    private val packagesInPath = listOf(
      PackagesInPath(
        RelativePath("./core/build/classes/kotlin/main/io"),
        listOf(PackageName("io.redgreen.cardbox"))
      ),

      PackagesInPath(
        RelativePath("./core/build/classes/kotlin/main/org"),
        listOf(PackageName("org.asm.java.bytecode"))
      ),
    )

    private val shellCommand = JarToolShellCommand
      .from(ArtifactName("core-kotlin-main.jar"), packagesInPath)

    @Test
    fun program() {
      assertThat(shellCommand.program)
        .isEqualTo("jar")
    }

    @Test
    fun arguments() {
      assertThat(shellCommand.arguments)
        .containsExactly(
          "-c", "--file", "core-kotlin-main.jar", "-C", "./core/build/classes/kotlin/main/", "io/", "org/"
        )
        .inOrder()
    }
  }
}
