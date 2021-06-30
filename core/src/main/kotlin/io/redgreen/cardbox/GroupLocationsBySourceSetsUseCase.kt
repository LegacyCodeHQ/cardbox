package io.redgreen.cardbox

import io.redgreen.cardbox.model.ClassFilesLocation
import io.redgreen.cardbox.model.RelativePath
import io.redgreen.cardbox.model.SourceSet
import java.io.File

class GroupLocationsBySourceSetsUseCase {
  private val classFilesLocationUseCase by lazy { ClassFilesLocationUseCase() }

  fun invoke(classFilesDirectoryPaths: Set<RelativePath>): Map<SourceSet, List<ClassFilesLocation>> {
    return classFilesDirectoryPaths
      .map { classFilesLocationUseCase.invoke(File(".${it.segment}")) }
      .groupBy { it.sourceSet }
  }
}
