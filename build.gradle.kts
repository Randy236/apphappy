// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.sonarqube") version "5.1.0.4882"
}

sonar {
    properties {
        // Evita fallo del job si el QG tarda; la cobertura igual se sube con sonarcloud-github-action
        property("sonar.qualitygate.wait", "false")
    }
}