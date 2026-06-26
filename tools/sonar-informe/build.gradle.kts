plugins {
    java
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
}

application {
    mainClass.set("com.happyjump.sonarinforme.Main")
}

tasks.register<Jar>("fatJar") {
    group = "build"
    description = "JAR ejecutable con dependencias (informe Sonar Word + Excel)"
    archiveBaseName.set("sonar-informe")
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "com.happyjump.sonarinforme.Main"
    }
    from(sourceSets.main.get().output)
    dependsOn(tasks.classes)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}

tasks.build {
    dependsOn("fatJar")
}
