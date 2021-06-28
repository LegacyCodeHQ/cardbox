package io.redgreen.cardbox

import io.redgreen.cardbox.PackageNameFromClassUseCase.Result
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.NotClassFile
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.File

class PackageNameFromDirectoryUseCase {
  private val packageNameFromClassUseCase = PackageNameFromClassUseCase()

  fun invoke(classFilesDirectory: File): ClassDirectoryPackageNameAssociation {
    val firstClassFile = classFilesDirectory.listFiles()!!.first { it.isFile }
    return ClassDirectoryPackageNameAssociation(
      classFilesDirectory,
      packageNameFromClassUseCase.invoke(firstClassFile.inputStream())
    )
  }

  data class ClassDirectoryPackageNameAssociation(
    val classFilesDirectory: File,
    val result: Result
  ) {
    companion object {
      private const val DOT = '.'
      private val SEPARATOR = File.separatorChar
    }

    val jarToolPath: File by lazy {
      when (result) {
        is PackageName -> findJarToolPath(result)
        DefaultPackage -> classFilesDirectory
        NotClassFile -> throw IllegalStateException("This cannot happen!")
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
  }
}
