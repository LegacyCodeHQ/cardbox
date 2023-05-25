plugins {
  kotlin("jvm")
  id("java-library")
  id("io.gitlab.arturbosch.detekt")
}

dependencies {
  testImplementation(kotlin("test-junit5"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")

  testImplementation("com.google.truth:truth:1.1.3")
}
