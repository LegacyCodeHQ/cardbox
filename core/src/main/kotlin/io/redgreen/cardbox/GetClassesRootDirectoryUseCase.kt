package io.redgreen.cardbox

import io.redgreen.cardbox.GetClassesRootDirectoryUseCase.Association.SourceSet.PRODUCTION
import io.redgreen.cardbox.GetClassesRootDirectoryUseCase.Association.SourceSet.TEST
import io.redgreen.cardbox.GetClassesRootDirectoryUseCase.Association.SourceSet.UNDETERMINED
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.NotClassFile
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.File

class GetClassesRootDirectoryUseCase {
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
    val result: Result
  ) {
    enum class SourceSet {
      TEST,
      PRODUCTION,
      UNDETERMINED
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
      when (result) {
        is PackageName -> findJarToolPath(result)
        DefaultPackage -> classFilesDirectory
        NotClassFile -> error("This cannot happen!")
      }
    }

    private fun packageNameMatchesDirectoryPath(path: String): Boolean =
      result is PackageName && path.contains(result.toPathSegment()) || result is DefaultPackage

    private fun findJarToolPath(packageName: PackageName): File {
      val packageNamePathSegment = packageName.toPathSegment()
      val classFilesDirectoryPath = classFilesDirectory.toString()
      val packageRootDirectoryPath = classFilesDirectoryPath
        .substring(0, classFilesDirectoryPath.indexOf(packageNamePathSegment))
      val indexOfSeparator = packageNamePathSegment.indexOf(SEPARATOR)

      val singleIdentifierPackageName = indexOfSeparator == -1
      val packageDirectoryName = if (singleIdentifierPackageName) {
        packageNamePathSegment
      } else {
        packageNamePathSegment.substring(0, indexOfSeparator)
      }

      return File("$packageRootDirectoryPath$SEPARATOR$packageDirectoryName")
    }

    companion object {
      private val SEPARATOR = File.separatorChar

      private const val SOURCE_SET_TEST_DIRECTORY = "test"
      private val REGEX_TEST_SOURCE_SET = Regex(".*${SEPARATOR}$SOURCE_SET_TEST_DIRECTORY(${SEPARATOR})?.*")

      private const val SOURCE_SET_PRODUCTION_DIRECTORY = "main"
      private val REGEX_PRODUCTION_SOURCE_SET = Regex(".*${SEPARATOR}$SOURCE_SET_PRODUCTION_DIRECTORY(${SEPARATOR})?.*")
    }
  }

  companion object {
    private const val CLASS_FILE_EXTENSION = "class"
  }
}
