import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.5.20" apply false
  id("io.gitlab.arturbosch.detekt") version "1.17.1"
  id("com.github.ben-manes.versions") version "0.46.0"
}

allprojects {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }

  tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
  }

  tasks.withType<KotlinCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()

    kotlinOptions {
      jvmTarget = "1.8"
      apiVersion = "1.5"

      freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
  }

  tasks.withType<Test> {
    useJUnitPlatform()
  }

  tasks.withType<Detekt>().configureEach {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    jvmTarget = "1.8"
  }
}

subprojects {
  subprojects {
    detekt {
      buildUponDefaultConfig = true
      allRules = false
      config = files("$rootDir/config/detekt.yml")
      baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt

      reports {
        html.enabled = true // observe findings in your browser with structure and code snippets
        xml.enabled = true // checkstyle like format mainly for integrations like Jenkins
        sarif.enabled = true // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
      }
    }
  }
}
