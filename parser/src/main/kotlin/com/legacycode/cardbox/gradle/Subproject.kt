package com.legacycode.cardbox.gradle

fun extractSubprojects(content: String): List<String> {
  val modulePattern1 = Regex("""include\([\s\S]*?\)""")
  val modulePattern2 = Regex("""include '(.+?)'""")
  val projectNamePattern = Regex("""project\(':(.+?)'\)\.name = '(.+?)'""")

  val moduleNames = mutableMapOf<String, String>()

  val includeMatches1 = modulePattern1.findAll(content)
  includeMatches1.forEach { matchResult ->
    val modules = matchResult.value
      .removePrefix("include(")
      .removeSuffix(")")
      .split(",")
      .map { it.trim().removeSurrounding("\"").removePrefix(":") }
      .filter { it.isNotEmpty() }

    modules.forEach { moduleName ->
      moduleNames[moduleName] = moduleName
    }
  }

  val includeMatches2 = modulePattern2.findAll(content)
  includeMatches2.map { it.groupValues[1].removePrefix(":") }
    .filter { it.isNotEmpty() }
    .forEach { moduleName ->
      moduleNames[moduleName] = moduleName
    }

  val projectNameMatches = projectNamePattern.findAll(content)
  projectNameMatches.forEach { matchResult ->
    val originalName = matchResult.groupValues[1]
    val newName = matchResult.groupValues[2]
    moduleNames[originalName] = newName
  }

  return moduleNames.values.toList()
}
