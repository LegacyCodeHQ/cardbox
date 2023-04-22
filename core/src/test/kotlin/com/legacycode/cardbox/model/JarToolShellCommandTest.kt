package com.legacycode.cardbox.model

import com.google.common.truth.Truth.assertThat
import com.legacycode.cardbox.model.PackageNameResult.DefaultPackage
import com.legacycode.cardbox.model.PackageNameResult.PackageName
import java.io.File
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class JarToolShellCommandTest {
  private val project = Project(File("/Users/PostIt"))

  @Nested
  inner class ToString {
    @Test
    fun `command for multiple packages (homogeneous)`() {
      // given
      val packagesInPath = listOf(
        PackagesInPath(
          RelativePath("./core/build/classes/kotlin/main/com"),
          listOf(PackageName("com.legacycode.cardbox"))
        ),

        PackagesInPath(
          RelativePath("./core/build/classes/kotlin/main/com"),
          listOf(PackageName("com.legacycode.cardbox.model"))
        ),
      )

      // when
      val shellCommand = JarToolShellCommand
        .from(project, packagesInPath, ArtifactName("core-kotlin-main.jar"))

      // then
      assertThat(shellCommand.text())
        .isEqualTo("jar -c --file core-kotlin-main.jar -C /Users/PostIt/core/build/classes/kotlin/main/ .")
    }

    @Test
    fun `command for multiple packages (heterogeneous)`() {
      // given
      val packagesInPath = listOf(
        PackagesInPath(
          RelativePath("./core/build/classes/kotlin/main/com"),
          listOf(PackageName("com.legacycode.cardbox"))
        ),

        PackagesInPath(
          RelativePath("./core/build/classes/kotlin/main/org"),
          listOf(PackageName("org.asm.java.bytecode"))
        ),
      )

      // when
      val shellCommand = JarToolShellCommand
        .from(project, packagesInPath, ArtifactName("core-kotlin-main.jar"))

      // then
      assertThat(shellCommand.text())
        .isEqualTo("jar -c --file core-kotlin-main.jar -C /Users/PostIt/core/build/classes/kotlin/main/ .")
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
          RelativePath("./build/classes/kotlin/test/com"),
          listOf(
            PackageName("com.legacycode.testassist"),
            PackageName("com.legacycode.cardbox"),
            PackageName("com.legacycode.cardbox.model")
          )
        ),

        PackagesInPath(
          RelativePath("./build/classes/kotlin/test/one"),
          listOf(PackageName("one"))
        ),
      )

      // when
      val shellCommand = JarToolShellCommand
        .from(project, packagesInPath, ArtifactName("build-kotlin-test.jar"))

      // then
      val expectedCommand = "jar -c --file build-kotlin-test.jar -C /Users/PostIt/build/classes/kotlin/test/ ."
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
      val shellCommand = JarToolShellCommand
        .from(project, packagesInPath, ArtifactName("build-java-test.jar"))

      // then
      val expectedCommand = "jar -c --file build-java-test.jar -C /Users/PostIt/build/classes/java/test/ ."
      assertThat(shellCommand.text())
        .isEqualTo(expectedCommand)
    }

    @Test
    fun `allow specifying artifact destination`() {
      // given
      val packagesInPath = listOf(
        PackagesInPath(
          RelativePath("./build/classes/java/test"),
          listOf(DefaultPackage),
        ),
      )

      // when
      val artifactName = ArtifactName("build-java-test.jar")
      val artifactsDestination = File("/Users/skrawberry/project-sha")
      val shellCommand = JarToolShellCommand
        .from(project, packagesInPath, artifactName, artifactsDestination)

      // then
      val expectedCommand =
        "jar -c --file /Users/skrawberry/project-sha/build-java-test.jar -C /Users/PostIt/build/classes/java/test/ ."
      assertThat(shellCommand.text())
        .isEqualTo(expectedCommand)
    }
  }

  @Nested
  inner class ProcessBuilder {
    // given
    private val packagesInPath = listOf(
      PackagesInPath(
        RelativePath("./core/build/classes/kotlin/main/com"),
        listOf(PackageName("com.legacycode.cardbox"))
      ),

      PackagesInPath(
        RelativePath("./core/build/classes/kotlin/main/org"),
        listOf(PackageName("org.asm.java.bytecode"))
      ),
    )

    private val shellCommand = JarToolShellCommand
      .from(project, packagesInPath, ArtifactName("core-kotlin-main.jar"))

    @Test
    fun program() {
      assertThat(shellCommand.program)
        .isEqualTo("jar")
    }

    @Test
    fun arguments() {
      assertThat(shellCommand.arguments)
        .containsExactly(
          "-c", "--file", "core-kotlin-main.jar", "-C", "/Users/PostIt/core/build/classes/kotlin/main/", "."
        )
        .inOrder()
    }

    @Test
    fun `arguments with artifact destination`() {
      // given
      val artifactName = ArtifactName("core-kotlin-main.jar")
      val artifactsDestination = File("/Users/skrawberry/project-sha")
      val shellCommand = JarToolShellCommand
        .from(project, packagesInPath, artifactName, artifactsDestination)

      // when & then
      assertThat(shellCommand.arguments)
        .containsExactly(
          "-c",
          "--file",
          "/Users/skrawberry/project-sha/core-kotlin-main.jar",
          "-C",
          "/Users/PostIt/core/build/classes/kotlin/main/",
          "."
        )
        .inOrder()
    }
  }
}
