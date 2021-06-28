package io.redgreen.cardbox.cli

import picocli.CommandLine

fun main(args: Array<String>) {
  CommandLine(CardboxCommand()).execute(*args)
}
