package io.redgreen.cardbox.model

import java.io.File

data class PackagesInPath(
  val path: RelativePath,
  val packageNameResults: List<PackageNameResult>
) {
  companion object {
    private val SEPARATOR = File.separator
    private const val EXTENSION_JAR = ".jar"
  }

  val artifactName: String
    get() {
      val identifiers = path.segment.split(SEPARATOR)
      return (listOf(identifiers[1]) + identifiers.takeLast(2))
        .joinToString("-", postfix = EXTENSION_JAR)
    }
}
