package com.legacycode.cardbox.cli

import java.util.Properties
import kotlin.system.exitProcess
import picocli.CommandLine

fun main(args: Array<String>) {
  val commandLine = CommandLine(CardboxCommand())
  val exitCode = commandLine.execute(*args)

  if (commandLine.isVersionHelpRequested) {
    printVersion()
    exitProcess(0)
  }

  exitProcess(exitCode)
}

fun printVersion() {
  val properties = CardboxCommand::class.java.classLoader
    .getResourceAsStream("version.properties").use { Properties().apply { load(it) } }
  println(properties["version"])
}
