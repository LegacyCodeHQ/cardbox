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
}
