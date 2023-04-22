package com.legacycode.cardbox.cli.commands

import com.legacycode.cardbox.DiscoverPotentialArtifactsUseCase
import com.legacycode.cardbox.model.ArtifactName
import com.legacycode.cardbox.model.JarToolShellCommand
import com.legacycode.cardbox.model.PackagesInPath
import com.legacycode.cardbox.model.Project
import com.legacycode.cardbox.model.SourceSet
import com.legacycode.cardbox.model.SourceSet.UNDETERMINED
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.RepositoryBuilder
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(
  name = "pack",
  mixinStandardHelpOptions = true,
  description = ["runs the JDK jar tool and produces .jar artifacts"]
)
class PackCommand : Runnable {
  companion object {
    private const val SOURCE_SET_UNDERLINE = "=========="

    private const val USER_HOME_KEY = "user.home"
    private const val ARTIFACT_DIRECTORY_NAME = "cardbox"

    private const val GIT_DIRECTORY = ".git"
    private const val EMOJI_PACKAGE = "\uD83D\uDCE6"

    private const val EMPTY_STRING = ""
    private const val REPO_SHA_LENGTH = 10
    private const val REPO_DIRTY_SUFFIX = "-dirty"
    private const val REPO_UNKNOWN_REVISION_SUFFIX = "-unknown"

    private const val DEBUG_PRINT_JAR_COMMAND = false
  }

  @Parameters(index = "0", description = ["directory"])
  lateinit var projectDirectory: File

  private val project by lazy {
    Project(projectDirectory)
  }

  private val outputDirectory: File by lazy {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMddyy"))
    val gitRevisionShaSuffix = gitRevisionShaSuffix ?: REPO_UNKNOWN_REVISION_SUFFIX
    val userHomeDirectory = File(System.getProperty(USER_HOME_KEY))

    userHomeDirectory
      .resolve(ARTIFACT_DIRECTORY_NAME)
      .resolve(project.name)
      .resolve("$date-$gitRevisionShaSuffix")
  }

  private val gitRevisionShaSuffix: String? by lazy {
    val repository = RepositoryBuilder()
      .setGitDir(projectDirectory.resolve(GIT_DIRECTORY))
      .build()
    val objectId = repository.resolve(Constants.HEAD)

    if (objectId != null) {
      val status = Git(repository).status().call()
      val suffix = if (status.isClean) EMPTY_STRING else REPO_DIRTY_SUFFIX
      "${objectId.abbreviate(REPO_SHA_LENGTH).name()}$suffix"
    } else {
      null
    }
  }

  override fun run() {
    val sourceSetsArtifacts = DiscoverPotentialArtifactsUseCase()
      .invoke(project)
      .toMutableMap()
    sourceSetsArtifacts.remove(UNDETERMINED)

    if (sourceSetsArtifacts.values.isEmpty()) {
      return
    }

    if (!outputDirectory.exists()) {
      outputDirectory.mkdirs()
    }

    printOutputDirectory(outputDirectory)
    createArtifacts(sourceSetsArtifacts)
  }

  private fun printOutputDirectory(outputDirectory: File) {
    println("[Writing artifacts to: ${outputDirectory.canonicalPath}]")
    println()
  }

  private fun createArtifacts(
    sourceSetsArtifacts: MutableMap<SourceSet, Map<ArtifactName, List<PackagesInPath>>>,
  ) {
    sourceSetsArtifacts.onEach { (sourceSet, artifactNamesPackagesInPathMap) ->
      println(sourceSet)
      println(SOURCE_SET_UNDERLINE)
      artifactNamesPackagesInPathMap.onEach { (artifactName, packagesInPath) ->
        println("$EMOJI_PACKAGE ${artifactName.value}")
        executeJarCommand(project, artifactName, packagesInPath)
      }
      println()
    }
  }

  private fun executeJarCommand(
    project: Project,
    artifactName: ArtifactName,
    packagesInPath: List<PackagesInPath>
  ) {
    val jarShellCommand = JarToolShellCommand
      .from(project, packagesInPath, artifactName, outputDirectory)
    if (DEBUG_PRINT_JAR_COMMAND) {
      println(jarShellCommand.text())
    }

    val processBuilder = ProcessBuilder(jarShellCommand.program, *jarShellCommand.arguments.toTypedArray())
    val process = processBuilder.start()
    val errorText = process.errorStream.bufferedReader().readText()
    if (errorText.isNotBlank()) {
      println(errorText)
    }
  }
}
