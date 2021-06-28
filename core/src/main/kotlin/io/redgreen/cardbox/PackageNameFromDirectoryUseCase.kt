package io.redgreen.cardbox

import io.redgreen.cardbox.PackageNameFromClassUseCase.Result
import java.io.File

class PackageNameFromDirectoryUseCase {
  private val packageNameFromClassUseCase = PackageNameFromClassUseCase()

  fun invoke(classFilesDirectory: File): Result {
    val firstClassFile = classFilesDirectory.listFiles()!!.first { it.isFile }
    return packageNameFromClassUseCase.invoke(firstClassFile.inputStream())
  }
}
