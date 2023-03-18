plugins {
  kotlin("jvm")
  id("io.gitlab.arturbosch.detekt")
}

dependencies {
  implementation("org.apache.bcel:bcel:6.5.0")

  implementation("com.orientechnologies:orientdb-client:3.2.0")
  implementation("com.orientechnologies:orientdb-core:3.2.0")
  implementation("com.orientechnologies:orientdb-server:3.2.0")
  implementation("com.orientechnologies:orientdb-tools:3.2.0")

  testImplementation(kotlin("test-junit5"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

  testImplementation("com.google.truth:truth:1.1.3")
}
