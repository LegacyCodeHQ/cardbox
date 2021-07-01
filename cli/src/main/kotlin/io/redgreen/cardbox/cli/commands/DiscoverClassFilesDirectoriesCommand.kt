package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.DiscoverClassFilesDirectoryPathsUseCase
import io.redgreen.cardbox.GroupClassFilesLocationsUseCase
import io.redgreen.cardbox.GroupPackagesByArtifactsUseCase
import io.redgreen.cardbox.GroupPackagesInPathsUseCase
import io.redgreen.cardbox.model.ArtifactName
import io.redgreen.cardbox.model.PackageNameResult
import io.redgreen.cardbox.model.PackagesInPath
import io.redgreen.cardbox.model.RelativePath
import io.redgreen.cardbox.model.SourceSet
import java.io.File
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

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
  private val groupPackagesByArtifactsUseCase = GroupPackagesByArtifactsUseCase()

  override fun run() {
    val classFilesDirectoryPaths = discoverClassFilesDirectoryPathsUseCase.invoke(directory)
    val sourceSetsLocationsMap = groupClassFilesLocationsUseCase.invoke(classFilesDirectoryPaths)
    val sourceSetsPackagesInPathMap = groupPackagesInPathsUseCase.invoke(sourceSetsLocationsMap)
    val sourceSetsArtifacts = groupPackagesByArtifactsUseCase.invoke(sourceSetsPackagesInPathMap)

    printSourceSetsArtifactInformation(sourceSetsArtifacts)
  }

  private fun printSourceSetsArtifactInformation(
    sourceSetsArtifacts: Map<SourceSet, Map<ArtifactName, List<PackagesInPath>>>
  ) {
    sourceSetsArtifacts.onEach { (sourceSet, artifactNamePackagesInPath) ->
      println(sourceSet)
      println("============")

      artifactNamePackagesInPath.onEach { (artifactName, packagesInPath) ->
        printArtifactContents(artifactName, packagesInPath)
      }
    }
  }

  private fun printArtifactContents(
    artifactName: ArtifactName,
    packagesInPath: List<PackagesInPath>
  ) {
    printArtifactHeader(artifactName)
    packagesInPath.onEach { (path, packageNameResults) ->
      printPackageNamesList(path, packageNameResults)
    }
    println()
  }

  private fun printArtifactHeader(artifactName: ArtifactName) {
    println("[$EMOJI_PACKAGE ${artifactName.value}]")
  }

  private fun printPackageNamesList(
    path: RelativePath,
    packageNameResults: List<PackageNameResult>
  ) {
    packageNameResults.onEachIndexed { index, packageNameResult ->
      val showPath = index == 0
      printPackageNameAndPathSegment(packageNameResult, path, showPath)
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
