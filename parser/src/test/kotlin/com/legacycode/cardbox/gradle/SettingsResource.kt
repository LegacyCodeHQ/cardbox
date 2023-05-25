package com.legacycode.cardbox.gradle

sealed class SettingsResource(
  private val subdirectory: String,
  private val filename: String,
) {
  val content: String
    get() {
      val resourcePath = "settings/$subdirectory/$filename"
      return KotlinDsl::class.java
        .classLoader
        .getResource(resourcePath)!!
        .readText()
    }

  class KotlinDsl(filename: String) : SettingsResource("kotlin", filename)
  class GroovyDsl(filename: String) : SettingsResource("groovy", filename)
}
