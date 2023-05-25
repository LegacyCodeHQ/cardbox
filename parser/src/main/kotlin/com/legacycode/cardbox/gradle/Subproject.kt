package com.legacycode.cardbox.gradle

data class Subproject(val name: String, val path: String)

fun extractSubprojects(content: String): List<Subproject> {
  val modulePattern1 = Regex("""include\([\s\S]*?\)""")
  val modulePattern2 = Regex("""include '(.+?)'""")
  val projectNamePattern = Regex("""project\(':(.+?)'\)\.name = '(.+?)'""")
  val projectDirPattern = Regex("""project\(':(.+?)'\)\.projectDir = file\('(.+?)'\)""")

  val moduleNames = mutableMapOf<String, Subproject>()

  val includeMatches1 = modulePattern1.findAll(content)
  includeMatches1.forEach { matchResult ->
    val modules = matchResult.value
      .removePrefix("include(")
      .removeSuffix(")")
      .split(",")
      .map { it.trim().removeSurrounding("\"").removePrefix(":") }
      .filter { it.isNotEmpty() }

    modules.forEach { moduleName ->
      val path = moduleName.replace(":", "/")
      moduleNames[moduleName] = Subproject(moduleName, path)
    }
  }

  val includeMatches2 = modulePattern2.findAll(content)
  includeMatches2.map { it.groupValues[1].removePrefix(":") }
    .filter { it.isNotEmpty() }
    .forEach { moduleName ->
      val path = moduleName.replace(":", "/")
      moduleNames[moduleName] = Subproject(moduleName, path)
    }

  val projectNameMatches = projectNamePattern.findAll(content)
  projectNameMatches.forEach { matchResult ->
    val originalName = matchResult.groupValues[1]
    val newName = matchResult.groupValues[2]
    moduleNames[originalName]?.let {
      moduleNames[originalName] = it.copy(name = newName)
    }
  }

  val projectDirMatches = projectDirPattern.findAll(content)
  projectDirMatches.forEach { matchResult ->
    val originalName = matchResult.groupValues[1]
    val newPath = matchResult.groupValues[2]
    moduleNames[originalName]?.let {
      moduleNames[originalName] = it.copy(path = newPath)
    }
  }

  return moduleNames.values.toList()
}
