package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.DiscoverClassFilesDirectoryPathsUseCase
import io.redgreen.cardbox.GroupLocationsBySourceSetsUseCase
import io.redgreen.cardbox.model.ClassFilesLocation
import io.redgreen.cardbox.model.PackageNameResult
import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import io.redgreen.cardbox.model.PackageNameResult.NotClassFile
import io.redgreen.cardbox.model.PackageNameResult.PackageName
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
  @Parameters(index = "0", description = ["directory"])
  lateinit var directory: File

  private val discoverClassFilesDirectoryPathsUseCase by lazy { DiscoverClassFilesDirectoryPathsUseCase() }
  private val groupLocationsBySourceSetsUseCase by lazy { GroupLocationsBySourceSetsUseCase() }

  override fun run() {
    val classFilesDirectoryPaths = discoverClassFilesDirectoryPathsUseCase.invoke(directory)
    val sourceSetsLocationsMap = groupLocationsBySourceSetsUseCase.invoke(classFilesDirectoryPaths)
    val sourceSetsPackagesInPathMap = associateSourceSetsWithLocationsPackageNames(sourceSetsLocationsMap)

    printSourcesSetsByLocation(sourceSetsPackagesInPathMap)
  }

  private fun associateSourceSetsWithLocationsPackageNames(
    sourceSetsLocationsMap: Map<SourceSet, List<ClassFilesLocation>>
  ): Map<SourceSet, List<PackagesInPath>> {
    return sourceSetsLocationsMap
      .map { (sourceSet, _) ->
        sourceSet to sourceSetsLocationsMap[sourceSet]!!.groupBy { RelativePath(it.jarToolPath.toString()) }
      }.associate { (sourceSet, jarToolPathClassFilesLocationsMap) ->
        val packagesInPath = jarToolPathClassFilesLocationsMap.map { (path, classFilesLocations) ->
          PackagesInPath(path, classFilesLocations.map(ClassFilesLocation::packageNameResult))
        }
        sourceSet to packagesInPath
      }
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
