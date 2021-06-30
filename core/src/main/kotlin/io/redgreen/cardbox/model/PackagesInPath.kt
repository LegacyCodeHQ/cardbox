package io.redgreen.cardbox.model

data class PackagesInPath(
  val path: RelativePath,
  val packageNameResults: List<PackageNameResult>
)
