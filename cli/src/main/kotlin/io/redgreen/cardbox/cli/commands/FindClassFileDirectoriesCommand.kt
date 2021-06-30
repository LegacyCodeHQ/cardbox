package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.FindDirectoriesContainingClassFilesUseCase
import io.redgreen.cardbox.GetClassesRootDirectoryUseCase
import io.redgreen.cardbox.GetClassesRootDirectoryUseCase.Association
import io.redgreen.cardbox.GetClassesRootDirectoryUseCase.Association.SourceSet
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.NotClassFile
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.File
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(
  name = "directories",
  mixinStandardHelpOptions = true,
  description = ["finds directories containing java .class files"]
)
class FindClassFileDirectoriesCommand : Runnable {
  @Parameters(index = "0", description = ["directory"])
  lateinit var directory: File

  private val findDirectoriesContainingClassFilesUseCase by lazy { FindDirectoriesContainingClassFilesUseCase() }
  private val getClassesRootDirectoryUseCase by lazy { GetClassesRootDirectoryUseCase() }

  override fun run() {
    val classFileDirectoryPaths = findDirectoriesContainingClassFilesUseCase.invoke(directory)
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
      .map { getClassesRootDirectoryUseCase.invoke(File(".$it")) }
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

  private fun printPackageName(result: Result) {
    when (result) {
      is PackageName -> println(result.value)
      DefaultPackage -> println("(default package)")
      NotClassFile -> println("Uh oh!")
    }
  }
}
