package com.happyjump.sonarinforme;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {
  public static void main(String[] args) throws Exception {
    String token = env("SONAR_TOKEN");
    String projectKey = arg(args, "--project", "Randy236_apphappy");
    Path outDir = Path.of(arg(args, "--out", "entregableunidad/entregable-04"));

    if (token == null || token.length() < 20) {
      System.err.println(
          """
          Falta SONAR_TOKEN.

          Uso:
            set SONAR_TOKEN=tu_token   (CMD)
            $env:SONAR_TOKEN="tu_token"   (PowerShell)
            java -jar sonar-informe-all.jar --out entregableunidad/entregable-04

          Token: https://sonarcloud.io → My Account → Security → Generate Token
          """);
      System.exit(1);
    }

    Files.createDirectories(outDir);
    System.out.println("Consultando SonarCloud (" + projectKey + ")...");
    SonarSnapshot snap = new SonarClient(token, projectKey).fetch();

    Path xlsx = outDir.resolve("METRICAS_SONAR_HAPPY_JUMP.xlsx");
    Path docx = outDir.resolve("INFORME_SONAR_HAPPY_JUMP.docx");
    ReportExporter.exportExcel(snap, xlsx);
    ReportExporter.exportWord(snap, docx);

    System.out.println();
    System.out.println("Listo. Archivos generados:");
    System.out.println("  " + xlsx.toAbsolutePath());
    System.out.println("  " + docx.toAbsolutePath());
    System.out.println();
    System.out.println("Resumen:");
    System.out.println("  Bugs: " + snap.measure("bugs") + " (Nivel " + snap.ratingLetter("reliability_rating") + ")");
    System.out.println(
        "  Code smells: " + snap.measure("code_smells") + " (Nivel " + snap.ratingLetter("sqale_rating") + ")");
    System.out.println("  Cobertura: " + snap.measure("coverage") + "%");
    System.out.println("  Hallazgos en informe: " + snap.issues().size());
  }

  private static String env(String name) {
    String v = System.getenv(name);
    return v == null ? null : v.trim();
  }

  private static String arg(String[] args, String flag, String defaultValue) {
    for (int i = 0; i < args.length - 1; i++) {
      if (flag.equals(args[i])) {
        return args[i + 1];
      }
    }
    return defaultValue;
  }
}
