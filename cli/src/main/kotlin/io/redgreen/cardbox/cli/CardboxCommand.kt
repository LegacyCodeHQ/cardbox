package io.redgreen.cardbox.cli

import io.redgreen.cardbox.cli.commands.DiscoverCommand
import io.redgreen.cardbox.cli.commands.PackCommand
import picocli.CommandLine.Command

internal const val TOOL_VERSION = "0.1.0"

@Command(
  name = "cardbox",
  mixinStandardHelpOptions = true,
  subcommands = [DiscoverCommand::class, PackCommand::class],
  description = ["Create JARs from Android projects."],
  commandListHeading = "%nCommands:%n%nAvailable commands are:%n",
  version = ["v$TOOL_VERSION"],
)
class CardboxCommand
