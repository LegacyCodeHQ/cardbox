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
    val sourceSetLocationsMap = groupLocationsBySourceSetsUseCase.invoke(classFilesDirectoryPaths)
    printSourcesSetsByLocation(sourceSetLocationsMap)
  }

  private fun printSourcesSetsByLocation(sourceSetLocationsMap: Map<SourceSet, List<ClassFilesLocation>>) {
    sourceSetLocationsMap.keys.onEach { sourceSet ->
      printLocationsForSourceSet(sourceSet, sourceSetLocationsMap[sourceSet]!!)
    }
  }

  private fun printLocationsForSourceSet(
    sourceSet: SourceSet,
    classFilesLocations: List<ClassFilesLocation>
  ) {
    println(sourceSet)
    println("============")
    val jarToolPathLocations = classFilesLocations.groupBy { it.jarToolPath }
    jarToolPathLocations.keys.onEach { jarToolPath ->
      println(jarToolPath)
      jarToolPathLocations[jarToolPath]!!
        .map(ClassFilesLocation::packageNameResult)
        .onEach(::printPackageName)
      println()
    }
  }

  private fun printPackageName(result: PackageNameResult) {
    when (result) {
      is PackageName -> println(result.value)
      DefaultPackage -> println("(default package)")
      NotClassFile -> println("Uh oh!")
    }
  }
}
