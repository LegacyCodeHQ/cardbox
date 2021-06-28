plugins {
  kotlin("jvm")
  id("application")
  id("io.gitlab.arturbosch.detekt")
}

application.mainClassName = "io.redgreen.cardbox.cli.CliKt"

dependencies {
  implementation(project(":core"))

  // Picocli
  implementation("info.picocli:picocli:4.6.1")

  // JUnit 5
  testImplementation(kotlin("test-junit5"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

  // Truth
  testImplementation("com.google.truth:truth:1.1.2")
}
