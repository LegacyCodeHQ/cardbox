package io.redgreen.cardbox

import io.redgreen.cardbox.model.ClassFilesLocation
import io.redgreen.cardbox.model.Project
import io.redgreen.cardbox.model.RelativePath
import io.redgreen.cardbox.model.SourceSet

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
