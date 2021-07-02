package io.redgreen.cardbox.model

import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import io.redgreen.cardbox.model.PackageNameResult.PackageName
import java.io.File

class JarToolShellCommand(
  private val artifactName: ArtifactName,
  private val packagesInPaths: List<PackagesInPath>
) {
  companion object {
    private const val DOT = '.'
    private const val PROGRAM = "jar"
    private const val SPACE = " "
    private const val EMPTY_STRING = ""
    private const val EXTENSION_CLASS = "class"

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

  private fun getClassPackageRootDirectories(): String {
    return packagesInPaths
      .flatMap { it.packageNameResults }
      .asSequence()
      .filter { it is PackageName }
      .map { it as PackageName }
      .map { it.value }
      .map { it.split(DOT).take(1).first() }
      .map { "$it${File.separatorChar}" }
      .distinct()
      .joinToString(SPACE)
  }

  private fun getClassesInDefaultPackages(packagesInPaths: List<PackagesInPath>): String {
    val classesBelongingToDefaultPackage = packagesInPaths
      .filter { (_, packageNameResults) -> packageNameResults.any { it is DefaultPackage } }
      .flatMap { (relativePath, _) ->
        val listFiles = File(relativePath.segment).listFiles()
        listFiles?.toList() ?: emptyList()
      }
      .filter { it.extension == EXTENSION_CLASS }
      .map { it.name }

    return if (classesBelongingToDefaultPackage.isEmpty()) {
      EMPTY_STRING
    } else {
      classesBelongingToDefaultPackage.joinToString(SPACE, prefix = SPACE)
    }
  }
}
