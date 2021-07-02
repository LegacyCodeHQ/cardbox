package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.DiscoverPotentialArtifactsUseCase
import io.redgreen.cardbox.model.JarToolShellCommand
import io.redgreen.cardbox.model.SourceSet.UNDETERMINED
import java.io.File
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(
  name = "pack",
  mixinStandardHelpOptions = true,
  description = ["runs the JDK jar tool and produces .jar artifacts"]
)
class PackCommand : Runnable {
  companion object {
    private const val USER_HOME_KEY = "user.home"
    private const val ARTIFACT_DIRECTORY_NAME = "cardbox"
  }

  @Parameters(index = "0", description = ["directory"])
  lateinit var directory: File

  private val outputDirectory: File by lazy {
    val projectName = directory.canonicalFile.name
    File(System.getProperty(USER_HOME_KEY)).resolve(ARTIFACT_DIRECTORY_NAME).resolve(projectName)
  }

  override fun run() {
    val sourceSetsArtifacts = DiscoverPotentialArtifactsUseCase()
      .invoke(directory)
      .toMutableMap()

    sourceSetsArtifacts.remove(UNDETERMINED)

    sourceSetsArtifacts.onEach { (sourceSet, artifactNamesPackagesInPathMap) ->
      println(sourceSet)
      println("==========")

      artifactNamesPackagesInPathMap.onEach { (artifactName, packagesInPath) ->
        println(artifactName.value)
        if (outputDirectory.exists().not()) {
          outputDirectory.mkdirs()
        }
        val jarShellCommand = JarToolShellCommand.from(artifactName, packagesInPath, outputDirectory)
        println(jarShellCommand.text())

        val processBuilder = ProcessBuilder(jarShellCommand.program, *jarShellCommand.arguments.toTypedArray())
        val process = processBuilder.start()
        println(process.errorStream.bufferedReader().readText())
      }
    }
  }
}
