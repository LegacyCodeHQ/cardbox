import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  kotlin("jvm")
  application
  id("io.gitlab.arturbosch.detekt")
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
  mavenCentral()
}

application.mainClassName = "io.redgreen.cardbox.cli.CliKt"

dependencies {
  implementation(project(":core"))

  // Picocli
  implementation("info.picocli:picocli:4.6.1")

  // JGit
  implementation("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r")

  // SLF4J
  implementation("org.slf4j:slf4j-simple:2.0.7") {
    because("JGit uses SLF4J and prints an error message on the console if this dependency is missing.")
  }

  // JUnit 5
  testImplementation(kotlin("test-junit5"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

  // Truth
  testImplementation("com.google.truth:truth:1.1.3")
}

tasks {
  named<ShadowJar>("shadowJar") {
    archiveBaseName.set("cardbox")
    mergeServiceFiles()

    manifest {
      attributes(mapOf("Main-Class" to "io.redgreen.cardbox.cli.CliKt"))
    }
  }
}

tasks {
  build {
    dependsOn(shadowJar)
  }
}

tasks.register("executable", DefaultTask::class) {
  description = "Creates self-executable file, that runs generated shadow jar"
  group = "Distribution"

  inputs.files(tasks.named("shadowJar"))
  outputs.file("${buildDir.resolve("exec").resolve("cardbox")}")

  doLast {
    val execFile = outputs.files.singleFile

    execFile.outputStream().use {
      it.write("#!/bin/sh\n\nexec java -Xmx512m -jar \"\$0\" \"\$@\"\n\n".toByteArray())
      it.write(inputs.files.singleFile.readBytes())
    }
  }
}
