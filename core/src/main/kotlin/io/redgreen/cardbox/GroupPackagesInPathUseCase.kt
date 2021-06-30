package io.redgreen.cardbox

import io.redgreen.cardbox.model.ClassFilesLocation
import io.redgreen.cardbox.model.PackagesInPath
import io.redgreen.cardbox.model.RelativePath
import io.redgreen.cardbox.model.SourceSet

class GroupPackagesInPathUseCase {
  fun invoke(
    sourceSetsClassFilesLocationsMap: Map<SourceSet, List<ClassFilesLocation>>
  ): Map<SourceSet, List<PackagesInPath>> {
    return sourceSetsClassFilesLocationsMap
      .map { (sourceSet, _) ->
        sourceSet to sourceSetsClassFilesLocationsMap[sourceSet]!!.groupBy { RelativePath(it.jarToolPath.toString()) }
      }.associate { (sourceSet, jarToolPathClassFilesLocationsMap) ->
        val packagesInPath = jarToolPathClassFilesLocationsMap.map { (path, classFilesLocations) ->
          PackagesInPath(path, classFilesLocations.map(ClassFilesLocation::packageNameResult))
        }
        sourceSet to packagesInPath
      }
  }
}
