package com.legacycode.cardbox.gradle

import java.io.File

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
