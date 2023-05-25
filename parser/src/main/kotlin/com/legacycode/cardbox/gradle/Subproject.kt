package com.legacycode.cardbox.gradle

data class Subproject(val name: String, val path: String)

fun extractSubprojects(content: String): List<Subproject> {
  val outSubprojectNames = mutableMapOf<String, Subproject>()

  extractIncludes(content, outSubprojectNames)
  updateProjectNames(content, outSubprojectNames)
  updateProjectDirs(content, outSubprojectNames)

  return outSubprojectNames.values.toList()
}

private fun extractIncludes(
  content: String,
  outSubprojectNames: MutableMap<String, Subproject>,
) {
  val subprojectPattern1 = Regex("""include\([\s\S]*?\)""")
  val subprojectPattern2 = Regex("""include '(.+?)'""")

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
      outSubprojectNames[subprojectName] = Subproject(subprojectName, path)
    }
  }

  val includeMatches2 = subprojectPattern2.findAll(content)
  includeMatches2.map { it.groupValues[1].removePrefix(":") }
    .filter { it.isNotEmpty() }
    .forEach { subprojectName ->
      val path = subprojectName.replace(":", "/")
      outSubprojectNames[subprojectName] = Subproject(subprojectName, path)
    }
}

private fun updateProjectNames(
  content: String,
  outSubprojectNames: MutableMap<String, Subproject>,
) {
  val projectNamePattern = Regex("""project\(':(.+?)'\)\.name = '(.+?)'""")

  val projectNameMatches = projectNamePattern.findAll(content)
  projectNameMatches.forEach { matchResult ->
    val originalName = matchResult.groupValues[1]
    val newName = matchResult.groupValues[2]
    outSubprojectNames[originalName]?.let {
      outSubprojectNames[originalName] = it.copy(name = newName)
    }
  }
}

private fun updateProjectDirs(
  content: String,
  outSubprojectNames: MutableMap<String, Subproject>,
) {
  val projectDirPattern = Regex("""project\(':(.+?)'\)\.projectDir = file\('(.+?)'\)""")

  val projectDirMatches = projectDirPattern.findAll(content)
  projectDirMatches.forEach { matchResult ->
    val originalName = matchResult.groupValues[1]
    val newPath = matchResult.groupValues[2]
    outSubprojectNames[originalName]?.let {
      outSubprojectNames[originalName] = it.copy(path = newPath)
    }
  }
}
