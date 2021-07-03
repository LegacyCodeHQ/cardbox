package io.redgreen.cardbox

import io.redgreen.cardbox.model.ClassFilesLocation
import io.redgreen.cardbox.model.RelativePath
import java.io.File

/**
 * Given a directory that contains compiled .class files, the use case returns the original directory and the package
 * name of the classes present inside the directory.
 */
class ClassFilesLocationUseCase {
  companion object {
    private const val CLASS_FILE_EXTENSION = "class"
  }

  private val packageNameFromClassUseCase = PackageNameFromClassUseCase()

  fun invoke(classFilesDirectory: File, classFilesPath: RelativePath): ClassFilesLocation {
    val firstClassFile = File(classFilesPath.segment).listFiles()!!
      .first { it.isFile && it.extension == CLASS_FILE_EXTENSION }
    return ClassFilesLocation(
      classFilesPath,
      packageNameFromClassUseCase.invoke(firstClassFile.inputStream())
    )
  }
}
