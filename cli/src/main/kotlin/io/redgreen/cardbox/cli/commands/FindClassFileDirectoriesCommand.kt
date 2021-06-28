package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.FindClassFileDirectoriesUseCase
import io.redgreen.cardbox.GetClassesRootDirectoryUseCase
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

  private val findClassFileDirectoriesUseCase by lazy { FindClassFileDirectoriesUseCase() }
  private val getClassesRootDirectoryUseCase by lazy { GetClassesRootDirectoryUseCase() }

  override fun run() {
    val classFileDirectoryPaths = findClassFileDirectoriesUseCase.invoke(directory)

    classFileDirectoryPaths
      .map { getClassesRootDirectoryUseCase.invoke(File(".$it")) }
      .onEach { println(it) }
  }
}
