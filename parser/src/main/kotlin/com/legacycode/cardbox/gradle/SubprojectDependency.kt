package com.legacycode.cardbox.gradle

data class SubprojectDependency(
  val name: String,
  val configuration: String,
)

fun extractSubprojectDependencies(content: String): List<SubprojectDependency> {
  val subprojectDependencyPattern = Regex("""(\w+)\(project\(":(.+?)"\)\)""")
  val matches = subprojectDependencyPattern.findAll(content)

  return matches.map { matchResult ->
    SubprojectDependency(
      name = matchResult.groupValues[2],
      configuration = matchResult.groupValues[1]
    )
  }.toList()
}
