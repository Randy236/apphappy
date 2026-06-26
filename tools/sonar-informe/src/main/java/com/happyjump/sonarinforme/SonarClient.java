package com.happyjump.sonarinforme;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SonarClient {
  private static final String HOST = "https://sonarcloud.io";

  private final String token;
  private final String projectKey;
  private final HttpClient http =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();

  public SonarClient(String token, String projectKey) {
    this.token = token;
    this.projectKey = projectKey;
  }

  public SonarSnapshot fetch() throws IOException, InterruptedException {
    Map<String, String> measures = fetchMeasures();
    JsonObject gate = fetchJson("/api/qualitygates/project_status?projectKey=" + projectKey);
    List<SonarIssue> issues = fetchIssues("BUG");
    issues.addAll(fetchIssues("CODE_SMELL"));
    issues.addAll(fetchIssues("VULNERABILITY"));
    return new SonarSnapshot(projectKey, measures, gate, issues);
  }

  private Map<String, String> fetchMeasures() throws IOException, InterruptedException {
    String keys =
        "bugs,code_smells,vulnerabilities,coverage,duplicated_lines_density,"
            + "reliability_rating,security_rating,sqale_rating,ncloc";
    JsonObject root =
        fetchJson("/api/measures/component?component=" + projectKey + "&metricKeys=" + keys);
    Map<String, String> out = new LinkedHashMap<>();
    JsonArray arr = root.getAsJsonObject("component").getAsJsonArray("measures");
    for (JsonElement el : arr) {
      JsonObject m = el.getAsJsonObject();
      out.put(m.get("metric").getAsString(), m.get("value").getAsString());
    }
    return out;
  }

  private List<SonarIssue> fetchIssues(String type) throws IOException, InterruptedException {
    List<SonarIssue> list = new ArrayList<>();
    int page = 1;
    int total;
    do {
      String path =
          "/api/issues/search?componentKeys="
              + projectKey
              + "&types="
              + type
              + "&ps=100&p="
              + page;
      JsonObject root = fetchJson(path);
      total = root.getAsJsonObject("paging").get("total").getAsInt();
      for (JsonElement el : root.getAsJsonArray("issues")) {
        JsonObject i = el.getAsJsonObject();
        String component = i.get("component").getAsString();
        String file = component.contains(":") ? component.substring(component.indexOf(':') + 1) : component;
        list.add(
            new SonarIssue(
                i.get("type").getAsString(),
                i.get("severity").getAsString(),
                i.get("status").getAsString(),
                file,
                i.has("line") && !i.get("line").isJsonNull() ? i.get("line").getAsInt() : 0,
                i.get("message").getAsString(),
                i.get("rule").getAsString()));
      }
      page++;
    } while (list.size() < total);
    return list;
  }

  private JsonObject fetchJson(String path) throws IOException, InterruptedException {
    HttpRequest req =
        HttpRequest.newBuilder()
            .uri(URI.create(HOST + path))
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/json")
            .GET()
            .build();
    HttpResponse<String> res =
        http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    if (res.statusCode() >= 400) {
      throw new IOException("SonarCloud HTTP " + res.statusCode() + ": " + res.body());
    }
    return JsonParser.parseString(res.body()).getAsJsonObject();
  }
}
