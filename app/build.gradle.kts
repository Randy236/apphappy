import java.io.File
import java.util.Properties
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    jacoco
}

jacoco {
    toolVersion = "0.8.12"
}

/** JaCoCo XML declara DTD externo; sin esto falla al buscar report.dtd junto al XML. */
fun parseJacocoXmlRoot(file: File): Element {
    val factory = DocumentBuilderFactory.newInstance()
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    factory.isNamespaceAware = true
    return factory.newDocumentBuilder().parse(file).documentElement
}

val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
// Emulador: http://10.0.2.2:3000/  |  Celular en Wi‑Fi: IP de tu PC (ver local.properties)
val happyJumpApiBaseUrl =
    localProperties.getProperty("happyJump.api.baseUrl")?.trim()?.takeIf { it.isNotEmpty() }
        ?: "http://10.0.2.2:3000/"

android {
    namespace = "com.example.happyj"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.happyj"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_BASE_URL", "\"$happyJumpApiBaseUrl\"")
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")
}

tasks.withType<Test>().configureEach {
    extensions.configure(JacocoTaskExtension::class) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Informe JaCoCo (unit tests debug) — XML para Sonar, HTML local"

    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/*\$*\$*.*",
    )

    val kotlinClasses = layout.buildDirectory.dir("tmp/kotlin-classes/debug").get().asFile
    val javaClasses = layout.buildDirectory
        .dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")
        .get().asFile

    val classTrees = listOf(kotlinClasses, javaClasses)
        .filter { it.exists() }
        .map { tree ->
            fileTree(tree) { exclude(fileFilter) }
        }

    classDirectories.setFrom(classTrees)
    sourceDirectories.setFrom(files("$projectDir/src/main/java"))
    executionData.setFrom(
        fileTree(layout.buildDirectory.get()) {
            include(
                "jacoco/testDebugUnitTest.exec",
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
            )
        },
    )
}

/** Porcentaje total del informe JaCoCo (misma base que el HTML/XML; unit tests JVM). */
tasks.register("printJacocoTotals") {
    group = "verification"
    description =
        "Imprime cobertura total LINE e INSTRUCTION desde jacocoTestReport.xml (como un resumen tipo coverage.py)"
    dependsOn("jacocoTestReport")
    doLast {
        val f = layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.xml").get().asFile
        if (!f.exists()) {
            println("No hay XML. Ejecuta: .\\gradlew.bat :app:jacocoTestReport")
            return@doLast
        }
        val report = parseJacocoXmlRoot(f)

        fun pct(covered: Int, missed: Int): Double {
            val t = covered + missed
            return if (t == 0) 100.0 else 100.0 * covered / t
        }

        fun counterOn(el: Element, type: String): Pair<Int, Int>? {
            val children = el.childNodes
            for (i in 0 until children.length) {
                val n = children.item(i)
                if (n !is Element || n.tagName != "counter") continue
                if (n.getAttribute("type") != type) continue
                return n.getAttribute("covered").toInt() to n.getAttribute("missed").toInt()
            }
            return null
        }

        fun aggregatePackages(type: String): Pair<Int, Int> {
            var c = 0
            var m = 0
            val pkgs = report.getElementsByTagName("package")
            for (i in 0 until pkgs.length) {
                val p = counterOn(pkgs.item(i) as Element, type) ?: continue
                c += p.first
                m += p.second
            }
            return c to m
        }

        fun lineInstr(label: String, type: String) {
            val root = counterOn(report, type)
            val (cov, mis) = root ?: aggregatePackages(type)
            val total = cov + mis
            if (total == 0) {
                println("$label: sin líneas/instrucciones instrumentadas (¿sin clases en el informe?)")
                return
            }
            println("$label: ${"%.2f".format(pct(cov, mis))}%  (covered=$cov, missed=$mis)")
        }

        println("=== Cobertura total (JaCoCo, :app:testDebugUnitTest) ===")
        lineInstr("LINE (lo más parecido a % de líneas en Python)", "LINE")
        lineInstr("INSTRUCTION (detalle a nivel bytecode)", "INSTRUCTION")
        println()
        println("Incluye Kotlin/Java en src/main compilados en debug, excl. R/BuildConfig/tests.")
        println("Pantallas Compose sin pruebas que las ejecuten cuentan como líneas missed → baja el %.")
    }
}

/**
 * JaCoCo no traduce el HTML oficial. Este informe usa los mismos datos del XML pero con columnas en español.
 */
tasks.register("jacocoReportEspanol") {
    group = "verification"
    description =
        "Genera HTML en español: index.html (detalle) y tabla-lineas-funciones.html (líneas + funciones, estilo tabla simple)"
    dependsOn("jacocoTestReport")
    doLast {
        val xmlFile = layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.xml").get().asFile
        if (!xmlFile.exists()) {
            println("Falta el XML. Ejecuta antes: .\\gradlew.bat :app:jacocoTestReport")
            return@doLast
        }
        val report = parseJacocoXmlRoot(xmlFile)

        fun esc(s: String) =
            s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")

        fun counters(el: Element): Map<String, Pair<Int, Int>> {
            val map = mutableMapOf<String, Pair<Int, Int>>()
            val ch = el.childNodes
            for (i in 0 until ch.length) {
                val n = ch.item(i)
                if (n !is Element || n.tagName != "counter") continue
                val typ = n.getAttribute("type")
                val cov = n.getAttribute("covered").toIntOrNull() ?: 0
                val mis = n.getAttribute("missed").toIntOrNull() ?: 0
                map[typ] = cov to mis
            }
            return map
        }

        fun pct(cov: Int, mis: Int): String {
            val t = cov + mis
            return if (t == 0) "—" else "${"%.1f".format(100.0 * cov / t)}%"
        }

        fun cellInstr(m: Map<String, Pair<Int, Int>>, type: String): String {
            val p = m[type] ?: return "<td colspan=\"2\">—</td>"
            val (cov, mis) = p
            val tot = cov + mis
            return "<td class=\"num\">$mis / $tot</td><td class=\"pct\">${pct(cov, mis)}</td>"
        }

        fun cellPair(m: Map<String, Pair<Int, Int>>, type: String): String {
            val p = m[type] ?: return "<td class=\"num\">—</td>"
            val (cov, mis) = p
            val tot = cov + mis
            return "<td class=\"num\">$mis / $tot</td>"
        }

        val outDir = layout.buildDirectory.dir("reports/jacoco/cobertura-es").get().asFile
        outDir.mkdirs()
        val out = File(outDir, "index.html")

        val pkgs = mutableListOf<Pair<String, Map<String, Pair<Int, Int>>>>()
        val nodes = report.getElementsByTagName("package")
        for (i in 0 until nodes.length) {
            val pkg = nodes.item(i) as Element
            val name = pkg.getAttribute("name").replace('/', '.')
            pkgs.add(name to counters(pkg))
        }
        pkgs.sortBy { it.first }

        fun mergeCounters(rows: List<Map<String, Pair<Int, Int>>>): Map<String, Pair<Int, Int>> {
            val types = listOf("INSTRUCTION", "BRANCH", "COMPLEXITY", "LINE", "METHOD", "CLASS")
            val out = mutableMapOf<String, Pair<Int, Int>>()
            for (t in types) {
                var c = 0
                var m = 0
                for (r in rows) {
                    val p = r[t] ?: continue
                    c += p.first
                    m += p.second
                }
                if (c + m > 0) {
                    out[t] = c to m
                }
            }
            return out
        }

        val fromRoot = counters(report)
        val totalMap =
            if (fromRoot.containsKey("INSTRUCTION")) {
                fromRoot
            } else {
                mergeCounters(pkgs.map { it.second })
            }
        val sb = StringBuilder()
        sb.append(
            """
            <!DOCTYPE html>
            <html lang="es">
            <head>
            <meta charset="utf-8"/>
            <title>Cobertura de código (español) — Happy Jump</title>
            <style>
              body { font-family: system-ui, sans-serif; margin: 1.5rem; color: #1a1a2e; }
              h1 { font-size: 1.25rem; }
              p.note { color: #555; max-width: 52rem; font-size: 0.9rem; }
              table { border-collapse: collapse; margin-top: 1rem; font-size: 0.85rem; }
              th, td { border: 1px solid #ccc; padding: 0.35rem 0.5rem; text-align: left; }
              th { background: #f0f4f8; }
              tr.total { font-weight: bold; background: #e8f5e9; }
              td.num, td.pct { text-align: right; white-space: nowrap; }
              colgroup col.pkg { width: 14rem; }
            </style>
            </head>
            <body>
            <h1>Cobertura de pruebas unitarias (JVM)</h1>
            <p class="note">
              Mismos datos que el informe JaCoCo (<code>jacocoTestReport.xml</code>), con nombres de columnas en español.
              <strong>Instrucciones</strong> = bytecode ejecutado; <strong>Ramas</strong> = decisiones (<code>if</code>, <code>when</code>);
              <strong>Complejidad</strong> = complejidad ciclomática; <strong>Líneas</strong> = líneas fuente con trazas.
            </p>
            <table>
            <thead>
            <tr>
              <th>Paquete</th>
              <th colspan="2">Instrucciones<br/><span style="font-weight:normal">(no cubiertas · total · % cubierto)</span></th>
              <th colspan="2">Ramas<br/><span style="font-weight:normal">(no cubiertas · total · % cubierto)</span></th>
              <th>Complejidad<br/><span style="font-weight:normal">(no cubierta · total)</span></th>
              <th colspan="2">Líneas<br/><span style="font-weight:normal">(no cubiertas · total · % cubierto)</span></th>
              <th>Métodos<br/><span style="font-weight:normal">(no cubiertos · total)</span></th>
              <th>Clases<br/><span style="font-weight:normal">(no cubiertas · total)</span></th>
            </tr>
            </thead>
            <tbody>
            """.trimIndent(),
        )

        fun appendRow(classAttr: String, name: String, m: Map<String, Pair<Int, Int>>) {
            sb.append("<tr class=\"$classAttr\"><td>${esc(name)}</td>")
            sb.append(cellInstr(m, "INSTRUCTION"))
            sb.append(cellInstr(m, "BRANCH"))
            sb.append(cellPair(m, "COMPLEXITY"))
            sb.append(cellInstr(m, "LINE"))
            sb.append(cellPair(m, "METHOD"))
            sb.append(cellPair(m, "CLASS"))
            sb.append("</tr>")
        }

        for ((name, m) in pkgs) {
            appendRow("", name, m)
        }
        appendRow("total", "TOTAL (todo el módulo app)", totalMap)

        sb.append(
            """
            </tbody>
            </table>
            <p class="note">Generado por Gradle (<code>:app:jacocoReportEspanol</code>). Abre este archivo en el navegador.</p>
            </body>
            </html>
            """.trimIndent(),
        )

        out.writeText(sb.toString(), Charsets.UTF_8)
        println("Informe detallado (español): ${out.absolutePath}")

        // Tabla tipo “líneas + funciones” (estilo informes tipo Sonar/Codecov)
        fun modLabel(full: String) =
            full.removePrefix("com.example.happyj.").ifEmpty { full }

        fun pctVal(cov: Int, mis: Int): Double {
            val t = cov + mis
            return if (t == 0) 100.0 else 100.0 * cov / t
        }

        fun pctClass(p: Double) =
            when {
                p >= 80.0 -> "pct-good"
                p >= 50.0 -> "pct-warn"
                else -> "pct-bad"
            }

        fun cellsLineFunc(m: Map<String, Pair<Int, Int>>): String {
            val line = m["LINE"]
            val meth = m["METHOD"]
            val (lc, lt, lcls) =
                if (line != null) {
                    val (c, mis) = line
                    val t = c + mis
                    if (t == 0) {
                        Triple("0/0", "—", "")
                    } else {
                        val p = pctVal(c, mis)
                        Triple("$c/$t", "${"%.1f".format(p)}%", pctClass(p))
                    }
                } else {
                    Triple("—", "—", "")
                }
            val (mc, mt, mcls) =
                if (meth != null) {
                    val (c, mis) = meth
                    val t = c + mis
                    if (t == 0) {
                        Triple("0/0", "—", "")
                    } else {
                        val p = pctVal(c, mis)
                        Triple("$c/$t", "${"%.1f".format(p)}%", pctClass(p))
                    }
                } else {
                    Triple("—", "—", "")
                }
            return buildString {
                append("<td class=\"num\">$lc</td>")
                append("<td class=\"num $lcls\">$lt</td>")
                append("<td class=\"num\">$mc</td>")
                append("<td class=\"num $mcls\">$mt</td>")
            }
        }

        val simple = File(outDir, "tabla-lineas-funciones.html")
        val sb2 = StringBuilder()
        sb2.append(
            """
            <!DOCTYPE html>
            <html lang="es">
            <head>
            <meta charset="utf-8"/>
            <title>Cobertura — Líneas y funciones — Happy Jump</title>
            <style>
              body { font-family: system-ui, sans-serif; margin: 1.5rem; color: #1a1a2e; }
              h1 { font-size: 1.2rem; }
              p.note { color: #555; font-size: 0.88rem; max-width: 48rem; }
              table { border-collapse: collapse; margin-top: 1rem; font-size: 0.9rem; min-width: 36rem; }
              th, td { border: 1px solid #b39ddb; padding: 0.45rem 0.65rem; }
              th { background: #7e57c2; color: #fff; font-weight: 600; }
              tr.total td { font-weight: bold; background: #ede7f6; }
              td.num { text-align: right; }
              .pct-good { color: #1b5e20; }
              .pct-warn { color: #e65100; }
              .pct-bad { color: #b71c1c; }
            </style>
            </head>
            <body>
            <h1>Cobertura por módulo (líneas y funciones)</h1>
            <p class="note">
              Datos de JaCoCo tras <code>testDebugUnitTest</code>.
              <strong>Líneas</strong> = cubiertas/total con traza; <strong>Funciones</strong> = contador <code>METHOD</code> de JaCoCo (métodos JVM, no solo <code>fun</code> del fuente).
              Colores: verde ≥80&nbsp;%, naranja ≥50&nbsp;%, rojo &lt;50&nbsp;%.
            </p>
            <table>
            <thead>
            <tr>
              <th>Módulo (paquete)</th>
              <th>Líneas<br/><span style="font-weight:400;font-size:0.85em">(cubiertas / total)</span></th>
              <th>Cobertura<br/>líneas</th>
              <th>Funciones<br/><span style="font-weight:400;font-size:0.85em">(cubiertas / total)</span></th>
              <th>Cobertura<br/>funciones</th>
            </tr>
            </thead>
            <tbody>
            """.trimIndent(),
        )
        for ((name, m) in pkgs) {
            sb2.append("<tr><td>${esc(modLabel(name))}</td>${cellsLineFunc(m)}</tr>")
        }
        sb2.append("<tr class=\"total\"><td>${esc("TOTAL")}</td>${cellsLineFunc(totalMap)}</tr>")
        sb2.append(
            """
            </tbody>
            </table>
            <p class="note">Archivo: <code>tabla-lineas-funciones.html</code> · Generado con <code>:app:jacocoReportEspanol</code></p>
            </body>
            </html>
            """.trimIndent(),
        )
        simple.writeText(sb2.toString(), Charsets.UTF_8)
        println("Tabla líneas/funciones: ${simple.absolutePath}")
    }
}