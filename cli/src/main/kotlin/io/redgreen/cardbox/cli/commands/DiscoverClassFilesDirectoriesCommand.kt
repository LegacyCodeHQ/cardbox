package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.DiscoverClassFilesDirectoryPathsUseCase
import io.redgreen.cardbox.GroupLocationsBySourceSetsUseCase
import io.redgreen.cardbox.model.ClassFilesLocation
import io.redgreen.cardbox.model.PackageNameResult
import io.redgreen.cardbox.model.PackageNameResult.DefaultPackage
import io.redgreen.cardbox.model.PackageNameResult.NotClassFile
import io.redgreen.cardbox.model.PackageNameResult.PackageName
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
    val sourceSetsPackageNamesMap = associateSourceSetsWithLocationsPackageNames(sourceSetsLocationsMap)

    printSourcesSetsByLocation(sourceSetsPackageNamesMap)
  }

  private fun associateSourceSetsWithLocationsPackageNames(
    sourceSetsLocationsMap: Map<SourceSet, List<ClassFilesLocation>>
  ): Map<SourceSet, Map<File, List<PackageNameResult>>> {
    return sourceSetsLocationsMap
      .map { (sourceSet, _) ->
        sourceSet to sourceSetsLocationsMap[sourceSet]!!.groupBy { it.jarToolPath }
      }.associate { (sourceSet, jarToolPathClassFilesLocationsMap) ->
        sourceSet to jarToolPathClassFilesLocationsMap.mapValues { it.value.map(ClassFilesLocation::packageNameResult) }
      }
  }

  private fun printSourcesSetsByLocation(
    sourceSetsPackageNamesMap: Map<SourceSet, Map<File, List<PackageNameResult>>>
  ) {
    sourceSetsPackageNamesMap
      .onEach { (sourceSet, jarToolPathPackageNamesMap) ->
        println(sourceSet)
        println("============")
        jarToolPathPackageNamesMap.onEach { (jarToolPath, packageNameResults) ->
          printPackageNameResults(jarToolPath, packageNameResults)
        }
      }
  }

  private fun printPackageNameResults(
    jarToolPath: File,
    packageNameResults: List<PackageNameResult>
  ) {
    println(jarToolPath)
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
