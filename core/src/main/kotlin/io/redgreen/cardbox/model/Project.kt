package io.redgreen.cardbox.model

import java.io.File

data class Project(val directory: File) {
  val name: String by lazy { directory.canonicalFile.name }
}
