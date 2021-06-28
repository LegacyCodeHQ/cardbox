package io.redgreen.cardbox

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
    val result: PackageNameFromClassUseCase.Result
  )
}
