package io.redgreen.cardbox.cli.commands

import io.redgreen.cardbox.DiscoverPotentialArtifactsUseCase
import io.redgreen.cardbox.model.ArtifactName
import io.redgreen.cardbox.model.JarToolShellCommand
import io.redgreen.cardbox.model.PackagesInPath
import io.redgreen.cardbox.model.SourceSet
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
    private const val EMOJI_PACKAGE = "\uD83D\uDCE6"
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

    if (sourceSetsArtifacts.values.isNotEmpty()) {
      if (!outputDirectory.exists()) {
        outputDirectory.mkdirs()
      }
    }

    createArtifacts(sourceSetsArtifacts)
  }

  private fun createArtifacts(
    sourceSetsArtifacts: MutableMap<SourceSet, Map<ArtifactName, List<PackagesInPath>>>
  ) {
    sourceSetsArtifacts.onEach { (sourceSet, artifactNamesPackagesInPathMap) ->
      println(sourceSet)
      println("==========")
      artifactNamesPackagesInPathMap.onEach { (artifactName, packagesInPath) ->
        println("$EMOJI_PACKAGE ${artifactName.value}")
        executeJarCommand(artifactName, packagesInPath)
      }
    }
  }

  private fun executeJarCommand(
    artifactName: ArtifactName,
    packagesInPath: List<PackagesInPath>
  ) {
    val jarShellCommand = JarToolShellCommand.from(artifactName, packagesInPath, outputDirectory)
    println(jarShellCommand.text())

    val processBuilder = ProcessBuilder(jarShellCommand.program, *jarShellCommand.arguments.toTypedArray())
    val process = processBuilder.start()
    println(process.errorStream.bufferedReader().readText())
  }
}
