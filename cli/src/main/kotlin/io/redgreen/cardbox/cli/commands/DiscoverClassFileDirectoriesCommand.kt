package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.FindDirectoriesContainingClassFilesUseCase
import io.redgreen.cardbox.GuessClassFilesRootDirectoryFromPackageNameUseCase
import io.redgreen.cardbox.model.Association
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
  private val rootDirectoryFromPackageNameUseCase by lazy { GuessClassFilesRootDirectoryFromPackageNameUseCase() }

  override fun run() {
    val classFileDirectoryPaths = directoriesContainingClassFilesUseCase.invoke(directory)
    printSourcesSetsByAssociation(classFileDirectoryPaths)
  }

  private fun printSourcesSetsByAssociation(classFileDirectoryPaths: Set<String>) {
    val sourceSets = getSourceSets(classFileDirectoryPaths)

    sourceSets.keys.onEach { key ->
      printSummary(key, sourceSets[key]!!)
    }
  }

  private fun getSourceSets(
    classFileDirectoryPaths: Set<String>
  ): Map<SourceSet, List<Association>> {
    return classFileDirectoryPaths
      .map { rootDirectoryFromPackageNameUseCase.invoke(File(".$it")) }
      .groupBy { it.sourceSet }
  }

  private fun printSummary(
    key: SourceSet,
    associations: List<Association>
  ) {
    println(key)
    println("============")
    val jarToolPathAssociations = associations.groupBy { it.jarToolPath }
    jarToolPathAssociations.keys.onEach { jarToolPath ->
      println(jarToolPath)
      jarToolPathAssociations[jarToolPath]!!
        .map(Association::packageNameResult)
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
