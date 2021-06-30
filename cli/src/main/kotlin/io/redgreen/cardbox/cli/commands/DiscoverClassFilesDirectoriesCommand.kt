package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.DiscoverClassFilesDirectoryPathsUseCase
import io.redgreen.cardbox.GroupClassFilesLocationsUseCase
import io.redgreen.cardbox.GroupPackagesInPathsUseCase
import io.redgreen.cardbox.model.PackageNameResult
import io.redgreen.cardbox.model.PackagesInPath
import io.redgreen.cardbox.model.RelativePath
import io.redgreen.cardbox.model.SourceSet
import java.io.File
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

typealias ArtifactName = String

@Command(
  name = "discover",
  mixinStandardHelpOptions = true,
  description = ["finds directories containing java .class files"]
)
class DiscoverClassFilesDirectoriesCommand : Runnable {
  companion object {
    private const val EMOJI_PACKAGE = "\uD83D\uDCE6"
    private const val COLUMN_WIDTH = 120
  }

  @Parameters(index = "0", description = ["directory"])
  lateinit var directory: File

  private val discoverClassFilesDirectoryPathsUseCase = DiscoverClassFilesDirectoryPathsUseCase()
  private val groupClassFilesLocationsUseCase = GroupClassFilesLocationsUseCase()
  private val groupPackagesInPathsUseCase = GroupPackagesInPathsUseCase()

  override fun run() {
    val classFilesDirectoryPaths = discoverClassFilesDirectoryPathsUseCase.invoke(directory)
    val sourceSetsLocationsMap = groupClassFilesLocationsUseCase.invoke(classFilesDirectoryPaths)
    val sourceSetsPackagesInPathMap = groupPackagesInPathsUseCase.invoke(sourceSetsLocationsMap)
    val sourceSetsArtifacts = groupPackagesByArtifacts(sourceSetsPackagesInPathMap)

    printSourceSetsArtifactInformation(sourceSetsArtifacts)
  }

  private fun groupPackagesByArtifacts(
    sourceSetsPackagesInPathMap: Map<SourceSet, List<PackagesInPath>>
  ): Map<SourceSet, Map<String, List<PackagesInPath>>> {
    return sourceSetsPackagesInPathMap
      .mapValues { (_, packagesInPath) -> packagesInPath.groupBy { it.artifactName } }
  }

  private fun printSourceSetsArtifactInformation(
    sourceSetsArtifacts: Map<SourceSet, Map<ArtifactName, List<PackagesInPath>>>
  ) {
    sourceSetsArtifacts.onEach { (sourceSet, artifactNamePackagesInPath) ->
      println(sourceSet)
      println("============")

      artifactNamePackagesInPath
        .onEach { (artifactName, packagesInPath) ->
          println("[$EMOJI_PACKAGE ${artifactName}]")
          packagesInPath.onEach { (path, packageNameResults) ->
            packageNameResults.onEachIndexed { index, packageNameResult ->
              val showPath = index == 0
              printPackageNameAndPathSegment(packageNameResult, path, showPath)
            }
          }
          println()
        }
    }
  }

  private fun printPackageNameAndPathSegment(
    packageNameResult: PackageNameResult,
    path: RelativePath,
    showPath: Boolean
  ) {
    val displayName = packageNameResult.displayText
    val paddedPath = if (showPath) {
      " [${path.segment}]".padStart(COLUMN_WIDTH - displayName.length)
    } else {
      ""
    }
    println("$displayName$paddedPath")
  }
}
