package com.legacycode.cardbox.gradle

data class Subproject(val name: String, val path: String)

fun extractSubprojects(content: String): List<Subproject> {
  val subprojectPattern1 = Regex("""include\([\s\S]*?\)""")
  val subprojectPattern2 = Regex("""include '(.+?)'""")
  val projectNamePattern = Regex("""project\(':(.+?)'\)\.name = '(.+?)'""")
  val projectDirPattern = Regex("""project\(':(.+?)'\)\.projectDir = file\('(.+?)'\)""")

  val subprojectNames = mutableMapOf<String, Subproject>()

  val includeMatches1 = subprojectPattern1.findAll(content)
  includeMatches1.forEach { matchResult ->
    val subprojects = matchResult.value
      .removePrefix("include(")
      .removeSuffix(")")
      .split(",")
      .map { it.trim().removeSurrounding("\"").removePrefix(":") }
      .filter { it.isNotEmpty() }

    subprojects.forEach { subprojectName ->
      val path = subprojectName.replace(":", "/")
      subprojectNames[subprojectName] = Subproject(subprojectName, path)
    }
  }

  val includeMatches2 = subprojectPattern2.findAll(content)
  includeMatches2.map { it.groupValues[1].removePrefix(":") }
    .filter { it.isNotEmpty() }
    .forEach { subprojectName ->
      val path = subprojectName.replace(":", "/")
      subprojectNames[subprojectName] = Subproject(subprojectName, path)
    }

  val projectNameMatches = projectNamePattern.findAll(content)
  projectNameMatches.forEach { matchResult ->
    val originalName = matchResult.groupValues[1]
    val newName = matchResult.groupValues[2]
    subprojectNames[originalName]?.let {
      subprojectNames[originalName] = it.copy(name = newName)
    }
  }

  val projectDirMatches = projectDirPattern.findAll(content)
  projectDirMatches.forEach { matchResult ->
    val originalName = matchResult.groupValues[1]
    val newPath = matchResult.groupValues[2]
    subprojectNames[originalName]?.let {
      subprojectNames[originalName] = it.copy(path = newPath)
    }
  }

  return subprojectNames.values.toList()
}
