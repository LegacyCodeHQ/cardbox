package io.redgreen.cardbox

import java.io.File
import java.nio.file.Files
import kotlin.streams.toList

/**
 * Scans a directory recursively and returns all paths containing compiled .class files relative to the given directory.
 */
class FindDirectoriesContainingClassFilesUseCase {
  companion object {
    private const val EXTENSION_CLASS = "class"
  }

  fun invoke(directory: File): Set<String> {
    val normalizedWorkingDirectoryPath = directory.toPath().toAbsolutePath().normalize()

    return Files
      .walk(normalizedWorkingDirectoryPath)
      .filter { Files.isRegularFile(it) }
      .filter { it.toFile().extension == EXTENSION_CLASS }
      .map { it.parent.toAbsolutePath().toString() }
      .map { it.substring(normalizedWorkingDirectoryPath.toString().length) }
      .distinct()
      .toList()
      .toSet()
  }
}
