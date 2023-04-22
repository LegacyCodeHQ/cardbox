package com.legacycode.cardbox

import com.legacycode.cardbox.model.ArtifactName
import com.legacycode.cardbox.model.PackagesInPath
import com.legacycode.cardbox.model.Project
import com.legacycode.cardbox.model.SourceSet

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
