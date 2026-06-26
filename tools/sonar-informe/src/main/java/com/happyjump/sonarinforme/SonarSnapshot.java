package com.happyjump.sonarinforme;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;

public record SonarSnapshot(
    String projectKey, Map<String, String> measures, JsonObject qualityGate, List<SonarIssue> issues) {

  public String ratingLetter(String metric) {
    String raw = measures.getOrDefault(metric, "1.0");
    return switch (raw) {
      case "1.0" -> "A";
      case "2.0" -> "B";
      case "3.0" -> "C";
      case "4.0" -> "D";
      case "5.0" -> "E";
      default -> raw;
    };
  }

  public String measure(String key) {
    return measures.getOrDefault(key, "—");
  }

  public boolean qualityGateOk() {
    return "OK".equalsIgnoreCase(qualityGate.get("projectStatus").getAsJsonObject().get("status").getAsString());
  }

  public long openBugs() {
    return issues.stream().filter(i -> "BUG".equals(i.type()) && i.isOpen()).count();
  }

  public long openSmells() {
    return issues.stream().filter(i -> "CODE_SMELL".equals(i.type()) && i.isOpen()).count();
  }

  public long openVulns() {
    return issues.stream().filter(i -> "VULNERABILITY".equals(i.type()) && i.isOpen()).count();
  }
}
