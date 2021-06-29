import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class CoreCanaryTest {
  @Test
  fun `junit and truth are setup`() {
    assertThat(true)
      .isTrue()
  }
}
