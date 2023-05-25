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

class GradleProject(val root: File) {
  val subprojects: List<Subproject>
    get() {
      val settingsGradleFile = root.resolve("settings.gradle")
      val settingsContent = settingsGradleFile.readText()
      return extractSubprojects(settingsContent)
    }

  fun buildScript(subproject: Subproject): File {
    val subprojectPath = root.resolve(subproject.path)
    val groovyBuildscript = subprojectPath.resolve("build.gradle")
    return if (groovyBuildscript.exists()) {
      groovyBuildscript
    } else {
      subprojectPath.resolve("build.gradle.kts")
    }
  }
}
