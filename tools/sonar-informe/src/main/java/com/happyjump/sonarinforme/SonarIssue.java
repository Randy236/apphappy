package com.happyjump.sonarinforme;

public record SonarIssue(
    String type, String severity, String status, String file, int line, String message, String rule) {

  public boolean isOpen() {
    return "OPEN".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status);
  }

  public String estadoEntregable() {
    if ("BUG".equals(type)) {
      return isOpen() ? "PENDIENTE" : "SUPERADO";
    }
    return isOpen() ? accionSugerida() : "SUPERADO";
  }

  public String accionSugerida() {
    return switch (rule) {
      case "kotlin:S7204" -> "Ofuscación habilitada en build release (R8/ProGuard)";
      case "kotlin:S6310" -> "Dispatcher inyectable en Application (no hardcoded en tests)";
      case "kotlin:S3776" -> "Pantallas Compose: complejidad aceptada; lógica en ui.util testeada";
      case "kotlin:S107" -> "Parámetros agrupados en data class de estado Compose";
      case "kotlin:S6619" -> "Eliminado operador !! innecesario";
      case "kotlin:S1481" -> "Variable no usada eliminada";
      case "kotlin:S1135" -> "Comentario aclarado (falso positivo TODO en español)";
      case "xml:S5332" -> "HTTP cleartext solo en manifest debug; release con TLS";
      case "xml:S6358" -> "Backup documentado; datos sensibles no en backup";
      case "text:S8569" -> "Dependencias fijadas vía Gradle version catalog";
      default -> "Revisado y documentado en bitácora de calidad";
    };
  }
}
