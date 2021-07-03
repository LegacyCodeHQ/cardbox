package io.redgreen.cardbox.model

import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import io.redgreen.cardbox.model.PackageNameResult.NotClassFile
import io.redgreen.cardbox.model.PackageNameResult.PackageName
import io.redgreen.cardbox.model.SourceSet.PRODUCTION
import io.redgreen.cardbox.model.SourceSet.TEST
import io.redgreen.cardbox.model.SourceSet.UNDETERMINED
import java.io.File

data class ClassFilesLocation(
  val classFilesPath: RelativePath,
  val classFilesDirectory: File,
  val packageNameResult: PackageNameResult
) {
  companion object {
    private val SEPARATOR = File.separatorChar

    private const val SOURCE_SET_TEST_DIRECTORY = "test"
    private val REGEX_TEST_SOURCE_SET = Regex(".*$SEPARATOR$SOURCE_SET_TEST_DIRECTORY($SEPARATOR)?.*")
  }

  val sourceSet: SourceSet by lazy {
    val classFilesDirectoryPath = classFilesDirectory.toString()
    when {
      !packageNameMatchesDirectoryPath(classFilesDirectoryPath) -> UNDETERMINED
      !classFilesDirectoryPath.matches(REGEX_TEST_SOURCE_SET) -> PRODUCTION
      classFilesDirectoryPath.matches(REGEX_TEST_SOURCE_SET) -> TEST
      else -> UNDETERMINED
    }
  }

  val jarToolPath: File by lazy {
    when (packageNameResult) {
      is PackageName -> findJarToolPath(packageNameResult)
      DefaultPackage -> classFilesDirectory
      NotClassFile -> error("This cannot happen!")
    }
  }

  private fun packageNameMatchesDirectoryPath(path: String): Boolean = packageNameResult is PackageName &&
    path.contains(packageNameResult.toPathSegment()) || packageNameResult is DefaultPackage

  private fun findJarToolPath(packageName: PackageName): File {
    val packageNamePathSegment = packageName.toPathSegment()
    val classFilesDirectoryPath = classFilesDirectory.toString()
    val packageNamePathSegmentIndex = classFilesDirectoryPath.indexOf(packageNamePathSegment)
    val classFilePathDoesNotMatchPackageName = packageNamePathSegmentIndex == -1
    if (classFilePathDoesNotMatchPackageName) {
      return File(classFilesDirectoryPath)
    }

    val packageRootDirectoryPath = classFilesDirectoryPath.substring(0, packageNamePathSegmentIndex)
    val indexOfSeparator = packageNamePathSegment.indexOf(SEPARATOR)

    val singleIdentifierPackageName = indexOfSeparator == -1
    val packageDirectoryName = if (singleIdentifierPackageName) {
      packageNamePathSegment
    } else {
      packageNamePathSegment.substring(0, indexOfSeparator)
    }

    return File("$packageRootDirectoryPath$SEPARATOR$packageDirectoryName")
  }
}
