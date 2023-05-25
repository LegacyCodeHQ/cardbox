package com.legacycode.cardbox.gradle

class SettingsKtsResource(private val filename: String) {
  val content: String
    get() {
      val resourcePath = "settings/kts/$filename"
      return SettingsKtsResource::class.java
        .classLoader
        .getResource(resourcePath)!!
        .readText()
    }
}

class SettingsGroovyResource(private val filename: String) {
  val content: String
    get() {
      val resourcePath = "settings/groovy/$filename"
      return SettingsGroovyResource::class.java
        .classLoader
        .getResource(resourcePath)!!
        .readText()
    }
}
