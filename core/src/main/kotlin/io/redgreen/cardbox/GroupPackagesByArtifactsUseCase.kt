package io.redgreen.cardbox

import io.redgreen.cardbox.model.ArtifactName
import io.redgreen.cardbox.model.PackagesInPath
import io.redgreen.cardbox.model.SourceSet

internal class GroupPackagesByArtifactsUseCase {
  fun invoke(
    sourceSetsPackagesInPathMap: Map<SourceSet, List<PackagesInPath>>
  ): Map<SourceSet, Map<ArtifactName, List<PackagesInPath>>> {
    return sourceSetsPackagesInPathMap
      .mapValues { (_, packagesInPath) -> packagesInPath.groupBy { it.artifactName } }
  }
}
