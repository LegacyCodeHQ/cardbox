package com.legacycode.cardbox.gradle

sealed class BuildScriptResource(
  private val rootDirectory: String,
  private val subdirectory: String,
  private val filename: String,
) {
  val content: String
    get() {
      val resourcePath = "$rootDirectory/$subdirectory/$filename"
      return KotlinDsl::class.java
        .classLoader
        .getResource(resourcePath)!!
        .readText()
    }

  class KotlinDsl private constructor(rootDirectory: String, filename: String) :
    BuildScriptResource(rootDirectory, "kotlin", filename) {

    companion object {
      fun settings(filename: String): KotlinDsl {
        return KotlinDsl("settings", filename)
      }

      fun build(filename: String): KotlinDsl {
        return KotlinDsl("build_", filename)
      }
    }
  }

  class GroovyDsl(filename: String) :
    BuildScriptResource("settings", "groovy", filename)
}
