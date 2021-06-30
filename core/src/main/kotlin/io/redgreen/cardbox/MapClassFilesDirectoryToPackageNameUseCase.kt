package io.redgreen.cardbox

import io.redgreen.cardbox.model.Association
import java.io.File

/**
 * Given a directory that contains compiled .class files, the use case returns the original directory and the package
 * name of the classes present inside the directory.
 */
class MapClassFilesDirectoryToPackageNameUseCase {
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
}
