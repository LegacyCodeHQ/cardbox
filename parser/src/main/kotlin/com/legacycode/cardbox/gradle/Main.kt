package com.legacycode.cardbox.gradle

import java.io.File

fun main() {
  val project = File(System.getProperty("user.home")).resolve("GitHubProjects/Signal-Android")

  val gradleProject = GradleProject(project)
  val subprojects = gradleProject.subprojects
  subprojects.onEach { subproject ->
    println("${subproject.name} @ ${subproject.path}/")
    val buildScript = gradleProject.buildScript(subproject).readText()
    val dependencies = extractSubprojectDependencies(buildScript)

    dependencies.forEach { subprojectDependency ->
      println("  → ${subprojectDependency.name} (${subprojectDependency.configuration})")
    }
    println()
  }
}
