package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.FindClassFileDirectoriesUseCase
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

  private val useCase by lazy { FindClassFileDirectoriesUseCase() }

  override fun run() {
    val classFileDirectoryPaths = useCase.invoke(directory)

    classFileDirectoryPaths
      .onEach { println(it) }
  }
}
