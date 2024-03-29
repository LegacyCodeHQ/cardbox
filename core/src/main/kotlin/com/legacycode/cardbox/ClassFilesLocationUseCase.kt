package com.legacycode.cardbox

import com.legacycode.cardbox.model.ClassFilesLocation
import com.legacycode.cardbox.model.Project
import com.legacycode.cardbox.model.RelativePath

/**
 * Given a directory that contains compiled .class files, the use case returns the original directory and the package
 * name of the classes present inside the directory.
 */
class ClassFilesLocationUseCase {
  companion object {
    private const val CLASS_FILE_EXTENSION = "class"
  }

  private val packageNameFromClassUseCase = PackageNameFromClassUseCase()

  fun invoke(project: Project, classFilesPath: RelativePath): ClassFilesLocation {
    val firstClassFile = project.resolve(classFilesPath).listFiles()!!
      .first { it.isFile && it.extension == CLASS_FILE_EXTENSION }
    return ClassFilesLocation(
      classFilesPath,
      packageNameFromClassUseCase.invoke(firstClassFile.inputStream())
    )
  }
}
