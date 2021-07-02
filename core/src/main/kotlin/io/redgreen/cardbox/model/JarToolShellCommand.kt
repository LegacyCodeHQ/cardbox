package io.redgreen.cardbox.model

import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import java.io.File

class JarToolShellCommand(
  private val artifactName: ArtifactName,
  private val packagesInPaths: List<PackagesInPath>
) {
  companion object {
    private const val PROGRAM = "jar"
    private const val SPACE = " "

    fun from(
      artifactName: ArtifactName,
      packagesInPath: List<PackagesInPath>
    ): JarToolShellCommand {
      return JarToolShellCommand(artifactName, packagesInPath)
    }
  }

  val program: String by lazy { text().split(SPACE).first() }
  val arguments: List<String> by lazy { text().split(SPACE).drop(1) }

  fun text(): String {
    val classFilesRootDirectory = getClassesRootDirectory(packagesInPaths)
    return "$PROGRAM -c --file ${artifactName.value} -C $classFilesRootDirectory ."
      .trim()
  }

  private fun getClassesRootDirectory(packagesInPaths: List<PackagesInPath>): String {
    val rootPaths = packagesInPaths.groupBy {
      val pathSegment = it.path.segment
      val containsClassesInDefaultPackage = packagesInPaths.any { packagesInPath ->
        packagesInPath.packageNameResults.contains(DefaultPackage)
      }

      if (containsClassesInDefaultPackage) {
        pathSegment
      } else {
        pathSegment.substring(0, pathSegment.lastIndexOf(File.separatorChar) + 1)
      }
    }.keys

    return if (rootPaths.size == 1) {
      rootPaths.first()
    } else {
      "${rootPaths.minByOrNull { it.length }!!}${File.separator}"
    }
  }
}
