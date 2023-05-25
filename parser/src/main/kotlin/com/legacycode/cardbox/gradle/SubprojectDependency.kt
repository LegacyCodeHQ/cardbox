package com.legacycode.cardbox.gradle

data class SubprojectDependency(
  val name: String,
  val configuration: String,
)

fun extractSubprojectDependencies(content: String): List<SubprojectDependency> {
  val subprojectDependencyPattern = """(\w+)\s*\(?\s*project\(\s*(['"]):?([^'"]*)\2\s*\)\s*\)?""".toRegex()
  val matches = subprojectDependencyPattern.findAll(content)

  return matches.map { matchResult ->
    SubprojectDependency(
      name = matchResult.groupValues[3],
      configuration = matchResult.groupValues[1]
    )
  }
    .distinct()
    .toList()
}
