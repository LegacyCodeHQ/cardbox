package com.legacycode.cardbox.model

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class ProjectTest {
  @Test
  fun `resolve paths starting with a slash`() {
    val resolvedFile = Project(File("/Users/lioness")).resolve(RelativePath("/witty"))
    assertThat(resolvedFile)
      .isEqualTo(File("/Users/lioness/witty"))
  }

  @Test
  fun `resolve paths starting with a dot slash`() {
    val resolvedFile = Project(File("/Users/lioness")).resolve(RelativePath("./witty"))
    assertThat(resolvedFile)
      .isEqualTo(File("/Users/lioness/witty"))
  }
}
