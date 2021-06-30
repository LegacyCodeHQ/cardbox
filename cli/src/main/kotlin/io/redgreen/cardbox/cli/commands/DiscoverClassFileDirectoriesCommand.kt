package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.FindDirectoriesContainingClassFilesUseCase
import io.redgreen.cardbox.ClassFilesLocationUseCase
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
class DiscoverClassFileDirectoriesCommand : Runnable {
  @Parameters(index = "0", description = ["directory"])
  lateinit var directory: File

  private val directoriesContainingClassFilesUseCase by lazy { FindDirectoriesContainingClassFilesUseCase() }
  private val classFilesLocationUseCase by lazy { ClassFilesLocationUseCase() }

  override fun run() {
    val classFileDirectoryPaths = directoriesContainingClassFilesUseCase.invoke(directory)
    printSourcesSetsByLocation(classFileDirectoryPaths)
  }

  private fun printSourcesSetsByLocation(classFileDirectoryPaths: Set<String>) {
    val sourceSets = groupLocationsBySourceSet(classFileDirectoryPaths)

    sourceSets.keys.onEach { key ->
      printSummary(key, sourceSets[key]!!)
    }
  }

  private fun groupLocationsBySourceSet(
    classFilesDirectoryPaths: Set<String>
  ): Map<SourceSet, List<ClassFilesLocation>> {
    return classFilesDirectoryPaths
      .map { classFilesLocationUseCase.invoke(File(".$it")) }
      .groupBy { it.sourceSet }
  }

  private fun printSummary(
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
        .onEach(this::printPackageName)
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
