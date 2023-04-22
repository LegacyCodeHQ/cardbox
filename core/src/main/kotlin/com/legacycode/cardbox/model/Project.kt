package com.legacycode.cardbox.model

import java.io.File

data class Project(val directory: File) {
  val name: String by lazy { directory.canonicalFile.name }

  fun resolve(relativePath: RelativePath): File {
    val charsToDrop = if (relativePath.segment.startsWith("./")) 2 else 1
    return directory.absoluteFile.resolve(relativePath.segment.drop(charsToDrop)).normalize()
  }
}
