package io.redgreen.cardbox

import io.redgreen.cardbox.model.ArtifactName
import io.redgreen.cardbox.model.PackagesInPath
import io.redgreen.cardbox.model.Project
import io.redgreen.cardbox.model.SourceSet

class DiscoverPotentialArtifactsUseCase {
  private val discoverClassFilesDirectoryPathsUseCase = DiscoverClassFilesDirectoryPathsUseCase()
  private val groupClassFilesLocationsUseCase = GroupClassFilesLocationsUseCase()
  private val groupPackagesInPathsUseCase = GroupPackagesInPathsUseCase()
  private val groupPackagesByArtifactsUseCase = GroupPackagesByArtifactsUseCase()

  fun invoke(project: Project): Map<SourceSet, Map<ArtifactName, List<PackagesInPath>>> {
    val classFilesDirectoryPaths = discoverClassFilesDirectoryPathsUseCase.invoke(project)
    val sourceSetsLocationsMap = groupClassFilesLocationsUseCase.invoke(project, classFilesDirectoryPaths)
    val sourceSetsPackagesInPathMap = groupPackagesInPathsUseCase.invoke(sourceSetsLocationsMap)

    return groupPackagesByArtifactsUseCase.invoke(sourceSetsPackagesInPathMap)
  }
}
