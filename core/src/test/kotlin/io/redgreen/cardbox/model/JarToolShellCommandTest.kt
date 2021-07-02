package io.redgreen.cardbox.model

import com.google.common.truth.Truth.assertThat
import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
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
      assertThat(shellCommand.text())
        .isEqualTo("jar -c --file core-kotlin-main.jar -C ./core/build/classes/kotlin/main/ .")
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
      assertThat(shellCommand.text())
        .isEqualTo("jar -c --file core-kotlin-main.jar -C ./core/build/classes/kotlin/main/ .")
    }

    @Test
    fun `command for multiple packages (heterogeneous, default package)`() {
      // given
      val packagesInPath = listOf(
        PackagesInPath(
          RelativePath("./build/classes/kotlin/test"),
          listOf(DefaultPackage),
        ),

        PackagesInPath(
          RelativePath("./build/classes/kotlin/test/io"),
          listOf(
            PackageName("io.redgreen.testassist"),
            PackageName("io.redgreen.cardbox"),
            PackageName("io.redgreen.cardbox.model")
          )
        ),

        PackagesInPath(
          RelativePath("./build/classes/kotlin/test/one"),
          listOf(PackageName("one"))
        ),
      )

      // when
      val shellCommand = JarToolShellCommand.from(ArtifactName("build-kotlin-test.jar"), packagesInPath)

      // then
      val expectedCommand = "jar -c --file build-kotlin-test.jar -C ./build/classes/kotlin/test/ ."
      assertThat(shellCommand.text())
        .isEqualTo(expectedCommand)
    }

    @Test
    fun `command for default package`() {
      // given
      val packagesInPath = listOf(
        PackagesInPath(
          RelativePath("./build/classes/java/test"),
          listOf(DefaultPackage),
        ),
      )

      // when
      val shellCommand = JarToolShellCommand.from(ArtifactName("build-java-test.jar"), packagesInPath)

      // then
      val expectedCommand = "jar -c --file build-java-test.jar -C ./build/classes/java/test ."
      assertThat(shellCommand.text())
        .isEqualTo(expectedCommand)
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
          "-c", "--file", "core-kotlin-main.jar", "-C", "./core/build/classes/kotlin/main/", "."
        )
        .inOrder()
    }
  }
}
