package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.DiscoverClassFilesDirectoryPathsUseCase
import io.redgreen.cardbox.GroupClassFilesLocationsUseCase
import io.redgreen.cardbox.GroupPackagesInPathsUseCase
import io.redgreen.cardbox.model.PackageNameResult
import io.redgreen.cardbox.model.PackagesInPath
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

    printSourcesSetsByLocation(sourceSetsPackagesInPathMap)
  }

  private fun printSourcesSetsByLocation(
    sourceSetsPackagesInPathMap: Map<SourceSet, List<PackagesInPath>>
  ) {
    sourceSetsPackagesInPathMap.onEach { (sourceSet, packagesInPaths) ->
      println(sourceSet)
      println("============")
      packagesInPaths.onEach(this::printPackagesInPath)
    }
  }

  private fun printPackagesInPath(packagesInPath: PackagesInPath) {
    val (relativePath, packageNameResults) = packagesInPath
    println("${relativePath.segment} => [$EMOJI_PACKAGE ${packagesInPath.artifactName}]")
    packageNameResults.map(PackageNameResult::displayText).onEach(::println)
    println()
  }
}
