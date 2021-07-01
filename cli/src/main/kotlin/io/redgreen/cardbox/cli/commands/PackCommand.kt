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
  @Parameters(index = "0", description = ["directory"])
  lateinit var directory: File

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
        val jarShellCommand = JarToolShellCommand.from(artifactName, packagesInPath)
        println(jarShellCommand)

        val processBuilder = ProcessBuilder(jarShellCommand.program, *jarShellCommand.arguments.toTypedArray())
        processBuilder.start()
      }
      println()
    }
    println()
  }
}
