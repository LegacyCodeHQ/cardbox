fun extractModules(fileContent: String): List<String> {
  val modulePattern1 = Regex("""include\([\s\S]*?\)""")
  val modulePattern2 = Regex("""include '(.+?)'""")
  val moduleNames = mutableListOf<String>()

  val includeMatches1 = modulePattern1.findAll(fileContent)
  includeMatches1.forEach { matchResult ->
    val modules = matchResult.value
      .removePrefix("include(")
      .removeSuffix(")")
      .split(",")
      .map { it.trim().removeSurrounding("\"").removePrefix(":") }
      .filter { it.isNotEmpty() }

    moduleNames.addAll(modules)
  }

  val includeMatches2 = modulePattern2.findAll(fileContent)
  includeMatches2.map { it.groupValues[1].removePrefix(":") }
    .filter { it.isNotEmpty() }
    .toCollection(moduleNames)

  return moduleNames
}
