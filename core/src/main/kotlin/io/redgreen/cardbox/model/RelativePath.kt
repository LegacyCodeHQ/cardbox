package io.redgreen.cardbox.model

import java.io.File

@JvmInline
value class RelativePath(val segment: String) {
  val parent: RelativePath
    get() {
      val parentPath = segment
        .split(File.separator)
        .dropLast(1)
        .joinToString(File.separator)

      return RelativePath(parentPath)
    }
}
