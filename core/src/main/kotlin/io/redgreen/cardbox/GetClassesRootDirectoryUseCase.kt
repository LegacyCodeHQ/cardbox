package io.redgreen.cardbox

import io.redgreen.cardbox.GetClassesRootDirectoryUseCase.Association.SourceSet.PRODUCTION
import io.redgreen.cardbox.GetClassesRootDirectoryUseCase.Association.SourceSet.TEST
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.NotClassFile
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.File

class GetClassesRootDirectoryUseCase {
  private val packageNameFromClassUseCase = PackageNameFromClassUseCase()

  fun invoke(classFilesDirectory: File): Association {
    val firstClassFile = classFilesDirectory.listFiles()!!.first { it.isFile }
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
      PRODUCTION
    }

    val sourceSet: SourceSet by lazy {
      val classFilesDirectoryPath = classFilesDirectory.toString()
      when  {
        classFilesDirectoryPath.matches(REGEX_PRODUCTION_SOURCE_SET) -> PRODUCTION
        classFilesDirectoryPath.matches(REGEX_TEST_SOURCE_SET) -> TEST
        else -> TODO()
      }
    }

    val jarToolPath: File by lazy {
      when (result) {
        is PackageName -> findJarToolPath(result)
        DefaultPackage -> classFilesDirectory
        NotClassFile -> error("This cannot happen!")
      }
    }

    private fun findJarToolPath(packageName: PackageName): File {
      val packageNamePathSegment = packageName.value.replace(DOT, SEPARATOR)
      val classFilesDirectoryPath = classFilesDirectory.toString()
      val packageRootDirectoryPath = classFilesDirectoryPath
        .substring(0, classFilesDirectoryPath.indexOf(packageNamePathSegment))
      val packageDirectoryName = packageNamePathSegment.substring(0, packageNamePathSegment.indexOf(SEPARATOR))

      return File("$packageRootDirectoryPath$SEPARATOR$packageDirectoryName")
    }

    companion object {
      private const val DOT = '.'
      private val SEPARATOR = File.separatorChar

      private const val SOURCE_SET_TEST_DIRECTORY = "test"
      private val REGEX_TEST_SOURCE_SET = Regex(".*${SEPARATOR}$SOURCE_SET_TEST_DIRECTORY(${SEPARATOR})?.*")

      private const val SOURCE_SET_PRODUCTION_DIRECTORY = "main"
      private val REGEX_PRODUCTION_SOURCE_SET = Regex(".*${SEPARATOR}$SOURCE_SET_PRODUCTION_DIRECTORY(${SEPARATOR})?.*")
    }
  }
}
