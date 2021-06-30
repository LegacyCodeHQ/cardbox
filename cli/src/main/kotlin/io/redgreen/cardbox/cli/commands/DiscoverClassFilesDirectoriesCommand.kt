package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.DiscoverClassFilesDirectoryPathsUseCase
import io.redgreen.cardbox.GroupClassFilesLocationsUseCase
import io.redgreen.cardbox.GroupPackagesInPathUseCase
import io.redgreen.cardbox.model.PackageNameResult
import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import io.redgreen.cardbox.model.PackageNameResult.NotClassFile
import io.redgreen.cardbox.model.PackageNameResult.PackageName
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
  @Parameters(index = "0", description = ["directory"])
  lateinit var directory: File

  private val discoverClassFilesDirectoryPathsUseCase = DiscoverClassFilesDirectoryPathsUseCase()
  private val groupClassFilesLocationsUseCase = GroupClassFilesLocationsUseCase()
  private val groupPackagesInPathUseCase = GroupPackagesInPathUseCase()

  override fun run() {
    val classFilesDirectoryPaths = discoverClassFilesDirectoryPathsUseCase.invoke(directory)
    val sourceSetsLocationsMap = groupClassFilesLocationsUseCase.invoke(classFilesDirectoryPaths)
    val sourceSetsPackagesInPathMap = groupPackagesInPathUseCase.invoke(sourceSetsLocationsMap)

    printSourcesSetsByLocation(sourceSetsPackagesInPathMap)
  }

  private fun printSourcesSetsByLocation(
    sourceSetsPackagesInPathMap: Map<SourceSet, List<PackagesInPath>>
  ) {
    sourceSetsPackagesInPathMap
      .onEach { (sourceSet, jarToolPathPackageNamesMap) ->
        println(sourceSet)
        println("============")
        jarToolPathPackageNamesMap.onEach { (path, packageNameResults) ->
          val packagesInPath = PackagesInPath(path, packageNameResults)
          printPackagesInPath(packagesInPath)
        }
      }
  }

  private fun printPackagesInPath(packagesInPath: PackagesInPath) {
    val (relativePath, packageNameResults) = packagesInPath
    println(relativePath.segment)
    packageNameResults.onEach(::printPackageName)
    println()
  }

  private fun printPackageName(result: PackageNameResult) {
    when (result) {
      is PackageName -> println(result.value)
      DefaultPackage -> println("(default package)")
      NotClassFile -> println("Uh oh!")
    }
  }
}
