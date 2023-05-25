package com.legacycode.cardbox.gradle

import java.io.File
import java.util.Locale

fun main() {
  val project = File(System.getProperty("user.home")).resolve("GitHubProjects/Signal-Android")

  val gradleProject = GradleProject(project)
  val subprojects = gradleProject.subprojects
  plantUml(gradleProject, subprojects)
}

private fun humanReadableOutput(
  gradleProject: GradleProject,
  subprojects: List<Subproject>,
) {
  subprojects.onEach { subproject ->
    val buildScript = gradleProject.buildScript(subproject).readText()
    val dependencies = extractSubprojectDependencies(buildScript)
    println("${subproject.name} (${subproject.path}/) [${dependencies.size}]")

    dependencies.forEach { subprojectDependency ->
      println("  → ${subprojectDependency.name} (${subprojectDependency.configuration})")
    }
    println()
  }
}

private fun plantUml(
  gradleProject: GradleProject,
  subprojects: List<Subproject>,
) {
  val groups = subprojects
    .filter { it.path.split("/").size > 1 }
    .groupBy { it.path.split("/").first() }
    .entries.filter { it.value.size > 1 }

  println("@startuml")
  for ((group, subprojectsInGroup) in groups) {
    println("""package "${toGroupName(group)}" {""")
    for (subproject in subprojectsInGroup) {
      println("  [${subproject.name}]")
    }
    println("}")
  }

  subprojects.onEach { subproject ->
    val buildScript = gradleProject.buildScript(subproject).readText()
    val dependencies = extractSubprojectDependencies(buildScript)

    dependencies
      .distinctBy { dependency -> dependency.name }
      .forEach { dependency ->
        println("[${subproject.name}] ..> [${dependency.name}]")
      }
  }
  println("@enduml")
}

private fun toGroupName(group: String): String {
  return group
    .replace('-', ' ')
    .uppercase(Locale.getDefault())
}
