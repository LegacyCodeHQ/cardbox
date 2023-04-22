package com.legacycode.cardbox.model

import com.legacycode.cardbox.model.PackageNameResult.DefaultPackage
import java.io.File

class JarToolShellCommand(
  private val project: Project,
  private val artifactName: ArtifactName,
  private val packagesInPaths: List<PackagesInPath>,
  private val artifactDestination: File? = null,
) {
  companion object {
    private const val PROGRAM = "jar"
    private const val SPACE = " "

    fun from(
      project: Project,
      packagesInPath: List<PackagesInPath>,
      artifactName: ArtifactName,
      artifactsDestination: File? = null
    ): JarToolShellCommand {
      return JarToolShellCommand(project, artifactName, packagesInPath, artifactsDestination)
    }
  }

  val program: String by lazy { text().split(SPACE).first() }
  val arguments: List<String> by lazy { text().split(SPACE).drop(1) }

  fun text(): String {
    val classFilesRootDirectory = project.resolve(RelativePath(getClassesRootDirectory(packagesInPaths)))

    val artifactFilePath = if (artifactDestination != null) {
      "${artifactDestination.canonicalPath}${File.separatorChar}${artifactName.value}"
    } else {
      artifactName.value
    }

    return "$PROGRAM -c --file $artifactFilePath -C $classFilesRootDirectory${File.separatorChar} ."
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
