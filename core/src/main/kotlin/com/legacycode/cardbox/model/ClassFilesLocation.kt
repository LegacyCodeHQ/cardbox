package com.legacycode.cardbox.model

import com.legacycode.cardbox.model.PackageNameResult.DefaultPackage
import com.legacycode.cardbox.model.PackageNameResult.NotClassFile
import com.legacycode.cardbox.model.PackageNameResult.PackageName
import com.legacycode.cardbox.model.SourceSet.PRODUCTION
import com.legacycode.cardbox.model.SourceSet.TEST
import com.legacycode.cardbox.model.SourceSet.UNDETERMINED
import java.io.File

data class ClassFilesLocation(
  val classFilesPath: RelativePath,
  val packageNameResult: PackageNameResult
) {
  companion object {
    private val SEPARATOR = File.separatorChar

    private const val SOURCE_SET_TEST_DIRECTORY = "test"
    private val REGEX_TEST_SOURCE_SET = Regex(".*$SEPARATOR$SOURCE_SET_TEST_DIRECTORY($SEPARATOR)?.*")
  }

  val sourceSet: SourceSet by lazy {
    when {
      !packageNameMatchesDirectoryPath(classFilesPath.segment) -> UNDETERMINED
      !classFilesPath.segment.matches(REGEX_TEST_SOURCE_SET) -> PRODUCTION
      classFilesPath.segment.matches(REGEX_TEST_SOURCE_SET) -> TEST
      else -> UNDETERMINED
    }
  }

  val pathForJarTool: File by lazy {
    when (packageNameResult) {
      is PackageName -> findPathForJarTool(packageNameResult)
      DefaultPackage -> File(classFilesPath.segment)
      NotClassFile -> error("This cannot happen!")
    }
  }

  private fun packageNameMatchesDirectoryPath(path: String): Boolean = packageNameResult is PackageName &&
    path.contains(packageNameResult.toPathSegment()) || packageNameResult is DefaultPackage

  private fun findPathForJarTool(packageName: PackageName): File {
    val packageNamePathSegment = packageName.toPathSegment()
    val classFilesDirectoryPath = classFilesPath.segment
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
