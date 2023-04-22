package com.legacycode.cardbox

import com.legacycode.cardbox.model.ClassFilesLocation
import com.legacycode.cardbox.model.Project
import com.legacycode.cardbox.model.RelativePath
import com.legacycode.cardbox.model.SourceSet

internal class GroupClassFilesLocationsUseCase {
  private val classFilesLocationUseCase by lazy { ClassFilesLocationUseCase() }

  fun invoke(
    project: Project,
    classFilesDirectoryPaths: Set<RelativePath>
  ): Map<SourceSet, List<ClassFilesLocation>> {
    return classFilesDirectoryPaths
      .map { classFilesLocationUseCase.invoke(project, it) }
      .groupBy { it.sourceSet }
  }
}
