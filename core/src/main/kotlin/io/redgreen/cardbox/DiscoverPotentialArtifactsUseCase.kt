package io.redgreen.cardbox

import io.redgreen.cardbox.model.ArtifactName
import io.redgreen.cardbox.model.PackagesInPath
import io.redgreen.cardbox.model.SourceSet
import java.io.File

class DiscoverPotentialArtifactsUseCase {
  private val discoverClassFilesDirectoryPathsUseCase = DiscoverClassFilesDirectoryPathsUseCase()
  private val groupClassFilesLocationsUseCase = GroupClassFilesLocationsUseCase()
  private val groupPackagesInPathsUseCase = GroupPackagesInPathsUseCase()
  private val groupPackagesByArtifactsUseCase = GroupPackagesByArtifactsUseCase()

  fun invoke(directory: File): Map<SourceSet, Map<ArtifactName, List<PackagesInPath>>> {
    val classFilesDirectoryPaths = discoverClassFilesDirectoryPathsUseCase.invoke(directory)
    val sourceSetsLocationsMap = groupClassFilesLocationsUseCase.invoke(classFilesDirectoryPaths)
    val sourceSetsPackagesInPathMap = groupPackagesInPathsUseCase.invoke(sourceSetsLocationsMap)

    return groupPackagesByArtifactsUseCase.invoke(sourceSetsPackagesInPathMap)
  }
}
