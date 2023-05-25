fun extractModules(fileContent: String): List<String> {
  val modulePattern = Regex("""include\([\s\S]*?\)""")
  val includeMatches = modulePattern.findAll(fileContent)
  val moduleNames = mutableListOf<String>()

  includeMatches.forEach { matchResult ->
    val modules = matchResult.value
      .removePrefix("include(")
      .removeSuffix(")")
      .split(",")
      .map { it.trim().removeSurrounding("\"").removePrefix(":") }

    moduleNames.addAll(modules)
  }

  return moduleNames
}
