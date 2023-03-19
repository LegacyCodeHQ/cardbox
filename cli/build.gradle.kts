import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.Properties
import org.jreleaser.model.Active

plugins {
  kotlin("jvm")
  application
  id("io.gitlab.arturbosch.detekt")
  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("org.jreleaser") version "1.2.0"
}

repositories {
  mavenCentral()
}

val mainClassFqn = "io.redgreen.cardbox.cli.CliKt"
val toolVersion = "0.3.0-SNAPSHOT"

application {
  applicationName = "cardbox"
  mainClassName = mainClassFqn
}

dependencies {
  implementation(project(":core"))

  // Picocli
  implementation("info.picocli:picocli:4.7.1")

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

tasks.withType<Jar> {
  manifest {
    attributes(
      "Main-Class" to mainClassFqn,
    )
  }
}

tasks {
  named<ShadowJar>("shadowJar") {
    archiveBaseName.set("cardbox")
    mergeServiceFiles()

    manifest {
      attributes(mapOf("Main-Class" to mainClassFqn))
    }
  }
}

tasks {
  build {
    dependsOn(shadowJar)
  }
}

tasks {
  named("classes") {
    dependsOn("createVersionFile")
  }

  create("createVersionFile") {
    dependsOn(processResources)
    doLast {
      val resourcesDirectory = File("$buildDir/resources/main/")
      if (!resourcesDirectory.exists()) {
        resourcesDirectory.mkdirs()
      }
      resourcesDirectory.resolve("version.properties").bufferedWriter().use { writer ->
        val properties = Properties()
        properties["version"] = toolVersion
        properties.store(writer, null)
      }
    }
  }
}

jreleaser {
  version = toolVersion
  gitRootSearch.set(true)

  project {
    name.set("cardbox")
    description.set("Create JARs from Android projects for jQAssistant.")
    website.set("https://github.com/redgreenio/cardbox")
    license.set("Apache-2.0")
    copyright.set("2021-Present, Ragunath Jawahar")
  }

  release {
    github {
      repoOwner.set("redgreenio")
      name.set("cardbox")
      branch.set("main")
      repoUrl.set("git@github.com:redgreenio/cardbox.git")

      tagName.set("cardbox-$toolVersion")
      releaseName.set("cardbox $toolVersion")
      overwrite.set(true)

      token.set(System.getenv("GITHUB_TOKEN"))
    }
  }

  distributions {
    create("cardbox") {
      artifact {
        path.set(File("build/distributions/cardbox-$toolVersion.zip"))
      }

      brew {
        active.set(Active.RELEASE)
        formulaName.set("cardbox")

        repoTap {
          repoOwner.set("redgreenio")
          name.set("homebrew-tap")
        }

        dependencies {
          dependency("openjdk@11")
        }

        extraProperties.put("skipJava", true)
      }
    }
  }
}
