package com.legacycode.cardbox.cli

import com.legacycode.cardbox.cli.commands.DiscoverCommand
import com.legacycode.cardbox.cli.commands.PackCommand
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
