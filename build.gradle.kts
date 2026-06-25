// Top-level build file where you can add configuration options common to all sub-projects/modules.
import java.io.File

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.sonarqube") version "5.1.0.4882"
}

dependencyLocking {
    lockAllConfigurations()
}

/** Resuelve dependencias del buildscript para generar gradle.lockfile en la raíz (Sonar S8569). */
configurations.register("rootDependencyLock") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    add("rootDependencyLock", "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:5.1.0.4882")
    add("rootDependencyLock", "com.android.tools.build:gradle:8.12.3")
    add("rootDependencyLock", "org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
}

subprojects {
    configurations.configureEach {
        resolutionStrategy.activateDependencyLocking()
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

/** Cobertura Sonar: solo lógica con pruebas unitarias JVM (ui.util). UI Compose queda fuera. */
val sonarCoverageExclusions =
    listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/ui/admin/**",
        "**/ui/cancha/**",
        "**/ui/login/**",
        "**/ui/main/**",
        "**/ui/perfil/**",
        "**/ui/salones/**",
        "**/ui/components/**",
        "**/ui/theme/**",
        "**/viewmodel/**",
        "**/data/**",
        "**/export/**",
        "**/notifications/**",
        "**/work/**",
        "**/HappyJumpApp.kt",
        "**/AppDispatchers.kt",
        "**/MainActivity.kt",
    ).joinToString(",")

val sonarJacocoXml =
    File(rootDir, "app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        .absolutePath.replace('\\', '/')
val sonarLintXml =
    File(rootDir, "app/build/reports/lint-results-debug.xml")
        .absolutePath.replace('\\', '/')
val sonarTestResults =
    File(rootDir, "app/build/test-results/testDebugUnitTest")
        .absolutePath.replace('\\', '/')
val sonarKotlinSources =
    File(rootDir, "app/src/main/java")
        .absolutePath.replace('\\', '/')
val sonarKotlinTests =
    File(rootDir, "app/src/test/java")
        .absolutePath.replace('\\', '/')
val sonarKotlinClasses =
    File(rootDir, "app/build/tmp/kotlin-classes/debug")
        .absolutePath.replace('\\', '/')
val sonarJavaClasses =
    File(rootDir, "app/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes")
        .absolutePath.replace('\\', '/')

sonar {
    properties {
        property("sonar.projectKey", "Randy236_apphappy")
        property("sonar.organization", "randy236")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.projectName", "apphappy")
        property("sonar.gradle.skipCompile", "true")
        property("sonar.qualitygate.wait", "false")
        property("sonar.coverage.jacoco.xmlReportPaths", sonarJacocoXml)
        property("sonar.androidLint.reportPaths", sonarLintXml)
        property("sonar.junit.reportPaths", sonarTestResults)
        property("sonar.kotlin.sourceDirs", sonarKotlinSources)
        property("sonar.test.kotlin.sourceDirs", sonarKotlinTests)
        property("sonar.sourceEncoding", "UTF-8")
        property(
            "sonar.java.binaries",
            listOf(sonarKotlinClasses, sonarJavaClasses).joinToString(","),
        )
        property("sonar.coverage.exclusions", sonarCoverageExclusions)
        property(
            "sonar.exclusions",
            "**/src/debug/**,**/debug/**,tools/sonar-informe/**,**/AppDispatchers.kt,**/HappyJumpApp.kt,**/*.lockfile,**/settings-gradle.lockfile",
        )
        property("sonar.issue.ignore.multicriteria", "compose_cc,compose_params,gradle_lock")
        property("sonar.issue.ignore.multicriteria.compose_cc.ruleKey", "kotlin:S3776")
        property("sonar.issue.ignore.multicriteria.compose_cc.resourceKey", "**/ui/**/*.kt")
        property("sonar.issue.ignore.multicriteria.compose_params.ruleKey", "kotlin:S107")
        property("sonar.issue.ignore.multicriteria.compose_params.resourceKey", "**/ui/**/*.kt")
        property("sonar.issue.ignore.multicriteria.gradle_lock.ruleKey", "text:S8569")
        property("sonar.issue.ignore.multicriteria.gradle_lock.resourceKey", "**/build.gradle.kts")
    }
}

/** Sonar solo importa cobertura si JaCoCo, Lint y bytecode debug existen antes de `sonar`. */
tasks.register("verifySonarPrerequisites") {
    group = "verification"
    description = "Comprueba XML JaCoCo, Lint y clases debug antes de subir a SonarCloud"
    dependsOn(":app:jacocoTestReport", ":app:lintDebug")
    doLast {
        val xml = layout.projectDirectory.file(
            "app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml",
        ).asFile
        val lint = layout.projectDirectory.file(
            "app/build/reports/lint-results-debug.xml",
        ).asFile
        val kotlinClasses = layout.projectDirectory.file("app/build/tmp/kotlin-classes/debug").asFile
        check(xml.exists() && xml.length() > 500) {
            "Falta JaCoCo XML. Ejecuta: .\\gradlew.bat :app:jacocoTestReport"
        }
        check(lint.exists() && lint.length() > 100) {
            "Falta informe Lint. Ejecuta: .\\gradlew.bat :app:lintDebug"
        }
        check(kotlinClasses.isDirectory) {
            "Faltan clases Kotlin debug. Ejecuta antes :app:testDebugUnitTest"
        }
        println(
            "Sonar prerequisites OK: JaCoCo ${xml.length()} bytes, " +
                "Lint ${lint.length()} bytes, kotlin-classes presente",
        )
    }
}

tasks.named("sonar") {
    dependsOn("verifySonarPrerequisites")
}