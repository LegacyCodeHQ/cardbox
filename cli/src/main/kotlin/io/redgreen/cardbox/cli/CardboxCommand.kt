package io.redgreen.cardbox.cli

import io.redgreen.cardbox.cli.commands.DiscoverClassFilesDirectoriesCommand
import picocli.CommandLine.Command

internal const val TOOL_VERSION = "0.0.1-dev"

@Command(
  name = "cardbox",
  mixinStandardHelpOptions = true,
  subcommands = [DiscoverClassFilesDirectoriesCommand::class],
  description = ["Accelerator for developing Timelapse features."],
  commandListHeading = "%nCommands:%n%nAvailable commands are:%n",
  version = ["v$TOOL_VERSION"],
)
class CardboxCommand
