package io.redgreen.cardbox.model

import io.redgreen.cardbox.model.PackageNameResult.PackageName
import java.io.File

class JarToolShellCommand(
  private val artifactName: ArtifactName,
  private val packagesInPath: List<PackagesInPath>
) {
  companion object {
    private const val DOT = '.'

    fun from(
      artifactName: ArtifactName,
      packagesInPath: List<PackagesInPath>
    ): JarToolShellCommand {
      return JarToolShellCommand(artifactName, packagesInPath)
    }
  }

  override fun toString(): String {
    val classFilesRootDirectory = getClassesRootDirectory(packagesInPath)
    val packageRootDirectories = packagesInPath
      .flatMap { it.packageNameResults }
      .asSequence()
      .map { it as PackageName }
      .map { it.value }
      .map { it.split(DOT).take(1).first() }
      .map { "$it${File.separatorChar}" }
      .distinct()
      .joinToString(" ")

    return "jar -c --file ${artifactName.value} -C $classFilesRootDirectory $packageRootDirectories"
  }

  private fun getClassesRootDirectory(packagesInPath: List<PackagesInPath>): String {
    val aClassFilesDirectoryPath = packagesInPath.first().path.segment
    return aClassFilesDirectoryPath
      .substring(0, aClassFilesDirectoryPath.lastIndexOf(File.separatorChar) + 1)
  }
}
