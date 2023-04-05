package io.redgreen.cardbox

import io.redgreen.cardbox.model.ClassFilesLocation
import io.redgreen.cardbox.model.PackagesInPath
import io.redgreen.cardbox.model.RelativePath
import io.redgreen.cardbox.model.SourceSet

internal class GroupPackagesInPathsUseCase {
  fun invoke(
    sourceSetsClassFilesLocationsMap: Map<SourceSet, List<ClassFilesLocation>>,
  ): Map<SourceSet, List<PackagesInPath>> {
    return sourceSetsClassFilesLocationsMap
      .map { (sourceSet, _) ->
        groupSourceSetsAndPathsForJarTool(sourceSet, sourceSetsClassFilesLocationsMap[sourceSet]!!)
      }.associate { (sourceSet, jarToolPathClassFilesLocationsMap) ->
        val packagesInPath = jarToolPathClassFilesLocationsMap.map { (path, classFilesLocations) ->
          PackagesInPath(path, classFilesLocations.map(ClassFilesLocation::packageNameResult))
        }
        sourceSet to packagesInPath
      }
  }

  private fun groupSourceSetsAndPathsForJarTool(
    sourceSet: SourceSet,
    classFilesLocations: List<ClassFilesLocation>,
  ): Pair<SourceSet, Map<RelativePath, List<ClassFilesLocation>>> {
    val pathsForJarToolAndClassFilesLocations = classFilesLocations
      .groupBy { classFilesLocation -> RelativePath(classFilesLocation.pathForJarTool.toString()) }

    return sourceSet to pathsForJarToolAndClassFilesLocations
  }
}
