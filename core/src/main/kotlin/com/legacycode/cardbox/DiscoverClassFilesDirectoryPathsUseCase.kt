package com.legacycode.cardbox

import com.legacycode.cardbox.model.Project
import com.legacycode.cardbox.model.RelativePath
import java.nio.file.Files
import kotlin.streams.toList

/**
 * Scans a directory recursively and returns all paths containing compiled .class files relative to the given directory.
 */
internal class DiscoverClassFilesDirectoryPathsUseCase {
  companion object {
    private const val EXTENSION_CLASS = "class"
  }

  fun invoke(project: Project): Set<RelativePath> {
    val normalizedWorkingDirectoryPath = project.directory.toPath().toAbsolutePath().normalize()

    return Files
      .walk(normalizedWorkingDirectoryPath)
      .filter { Files.isRegularFile(it) }
      .filter { it.toFile().extension == EXTENSION_CLASS }
      .map { it.parent.toAbsolutePath().toString() }
      .map { it.substring(normalizedWorkingDirectoryPath.toString().length) }
      .map { ".$it" }
      .map(::RelativePath)
      .distinct()
      .toList()
      .toSet()
  }
}
