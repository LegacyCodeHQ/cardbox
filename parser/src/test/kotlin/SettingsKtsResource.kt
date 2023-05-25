class SettingsKtsResource(private val filename: String) {
  val content: String
    get() {
      return SettingsKtsResource::class.java
        .classLoader
        .getResource("settings/kts/$filename")!!
        .readText()
    }
}
