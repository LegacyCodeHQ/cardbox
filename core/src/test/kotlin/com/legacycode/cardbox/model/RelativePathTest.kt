package com.legacycode.cardbox.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class RelativePathTest {
  @Test
  fun `retrieve parent`() {
    // given
    val path = RelativePath("./mastodon/build/intermediates/javac/debug/classes/org")

    // when & then
    assertThat(path.parent)
      .isEqualTo(RelativePath("./mastodon/build/intermediates/javac/debug/classes"))
  }
}
