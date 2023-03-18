package io.redgreen.cardbox.cli

import io.redgreen.cardbox.cli.commands.DiscoverCommand
import io.redgreen.cardbox.cli.commands.PackCommand
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(
  name = "cardbox",
  subcommands = [
    DiscoverCommand::class,
    PackCommand::class,
  ],
  description = ["Create JARs from Android projects for jQAssistant."],
  commandListHeading = "%nCommands:%n%nAvailable commands are:%n",
)
class CardboxCommand {
  @Option(
    names = ["--version", "-v"],
    description = ["prints version"],
    versionHelp = true,
  )
  var version: Boolean = false
}
