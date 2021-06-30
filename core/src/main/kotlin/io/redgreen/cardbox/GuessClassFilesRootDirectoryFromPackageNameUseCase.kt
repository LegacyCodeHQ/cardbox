package io.redgreen.cardbox

import io.redgreen.cardbox.GuessClassFilesRootDirectoryFromPackageNameUseCase.Association.SourceSet.PRODUCTION
import io.redgreen.cardbox.GuessClassFilesRootDirectoryFromPackageNameUseCase.Association.SourceSet.TEST
import io.redgreen.cardbox.GuessClassFilesRootDirectoryFromPackageNameUseCase.Association.SourceSet.UNDETERMINED
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.NotClassFile
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.File

/**
 * Given a directory that contains compiled .class files, the use case returns the original directory and the package
 * name of the classes present inside the directory.
 */
class GuessClassFilesRootDirectoryFromPackageNameUseCase {
  companion object {
    private const val CLASS_FILE_EXTENSION = "class"
  }

  private val packageNameFromClassUseCase = PackageNameFromClassUseCase()

  fun invoke(classFilesDirectory: File): Association {
    val firstClassFile = classFilesDirectory.listFiles()!!
      .first { it.isFile && it.extension == CLASS_FILE_EXTENSION }
    return Association(
      classFilesDirectory,
      packageNameFromClassUseCase.invoke(firstClassFile.inputStream())
    )
  }

  data class Association(
    val classFilesDirectory: File,
    val packageNameResult: Result
  ) {
    enum class SourceSet {
      TEST,
      PRODUCTION,
      UNDETERMINED
    }

    companion object {
      private val SEPARATOR = File.separatorChar

      private const val SOURCE_SET_TEST_DIRECTORY = "test"
      private val REGEX_TEST_SOURCE_SET = Regex(".*${SEPARATOR}$SOURCE_SET_TEST_DIRECTORY(${SEPARATOR})?.*")

      private const val SOURCE_SET_PRODUCTION_DIRECTORY = "main"
      private val REGEX_PRODUCTION_SOURCE_SET = Regex(".*${SEPARATOR}$SOURCE_SET_PRODUCTION_DIRECTORY(${SEPARATOR})?.*")
    }

    val sourceSet: SourceSet by lazy {
      val classFilesDirectoryPath = classFilesDirectory.toString()
      when {
        !packageNameMatchesDirectoryPath(classFilesDirectoryPath) -> UNDETERMINED
        classFilesDirectoryPath.matches(REGEX_PRODUCTION_SOURCE_SET) -> PRODUCTION
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

    private fun packageNameMatchesDirectoryPath(path: String): Boolean =
      packageNameResult is PackageName && path.contains(packageNameResult.toPathSegment()) || packageNameResult is DefaultPackage

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
}
