package com.legacycode.cardbox

import com.legacycode.cardbox.model.ArtifactName
import com.legacycode.cardbox.model.PackagesInPath
import com.legacycode.cardbox.model.SourceSet

internal class GroupPackagesByArtifactsUseCase {
  fun invoke(
    sourceSetsPackagesInPathMap: Map<SourceSet, List<PackagesInPath>>
  ): Map<SourceSet, Map<ArtifactName, List<PackagesInPath>>> {
    return sourceSetsPackagesInPathMap
      .mapValues { (_, packagesInPath) -> packagesInPath.groupBy { it.artifactName } }
  }
}
