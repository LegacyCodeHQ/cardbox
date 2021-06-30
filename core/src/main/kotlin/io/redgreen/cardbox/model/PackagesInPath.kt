package io.redgreen.cardbox.model

import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import java.io.File

data class PackagesInPath(
  val path: RelativePath,
  val packageNameResults: List<PackageNameResult>
) {
  companion object {
    private val SEPARATOR = File.separator
    private const val EXTENSION_JAR = ".jar"
  }

  val artifactName: ArtifactName
    get() {
      val pathContainsDefaultPackage = packageNameResults.first() is DefaultPackage

      val identifiers = path.segment.split(SEPARATOR)
      val finalIdentifiers = if (pathContainsDefaultPackage) {
        identifiers.takeLast(2)
      } else {
        identifiers.takeLast(3).dropLast(1)
      }

      val value = (listOf(identifiers[1]) + finalIdentifiers).joinToString("-", postfix = EXTENSION_JAR)
      return ArtifactName(value)
    }
}
