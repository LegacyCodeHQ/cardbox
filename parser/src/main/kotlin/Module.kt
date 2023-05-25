fun extractModules(fileContent: String): List<String> {
  val modulePattern = Regex("""include\("(.+?)"\)""")
  val moduleMatches = modulePattern.findAll(fileContent)

  return moduleMatches
    .map { matchResult -> matchResult.groupValues[1] }
    .toList()
}
