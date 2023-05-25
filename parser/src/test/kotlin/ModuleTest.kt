import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ModuleTest {
  @Test
  fun `extract module names`() {
    // given
    val settingsContent = """
      rootProject.name = "myproject"

      include("module1")
      include("module2")
      include("module3")
    """.trimIndent()

    // when
    val modules = extractModules(settingsContent)

    // then
    assertThat(modules)
      .containsExactly(
        "module1",
        "module2",
        "module3",
      )
      .inOrder()
  }

  @Test
  fun `sample 2`() {
    // given
    val settingsContent = """
      rootProject.name = "cardbox"
      include(
        "core",
        "cli",
        "parser"
      )
    """.trimIndent()

    // when
    val modules = extractModules(settingsContent)

    // then
    assertThat(modules)
      .containsExactly(
        "core",
        "cli",
        "parser",
      )
      .inOrder()
  }

  @Test
  fun `sample 3`() {
    // given
    val settingsContent = """
      rootProject.name = "cardbox"
      include(
        ":core",
        ":cli",
        ":parser"
      )
    """.trimIndent()

    // when
    val modules = extractModules(settingsContent)

    // then
    assertThat(modules)
      .containsExactly(
        "core",
        "cli",
        "parser",
      )
      .inOrder()
  }

  @Test
  fun `sample 4`() {
    val settingsContent = """
      rootProject.name = "tumbleweed"
      include(
        ":cli",
        ":bytecode:scanner",
        ":bytecode:samples",
        ":web-server",
        ":filesystem",
        ":vcs",
        ":bytecode:testing",
        ":web-client-react",
        ":android",
        ":viz",
      )
    """.trimIndent()

    // when
    val modules = extractModules(settingsContent)

    // then
    assertThat(modules)
      .containsExactly(
        "cli",
        "bytecode:scanner",
        "bytecode:samples",
        "web-server",
        "filesystem",
        "vcs",
        "bytecode:testing",
        "web-client-react",
        "android",
        "viz",
      )
      .inOrder()
  }
}