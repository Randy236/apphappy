#!/usr/bin/env bash
# Ejecuta localmente el pipeline (Linux/macOS/WSL)
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "=== 1. Pruebas API (Node) ==="
(cd server && (npm ci --ignore-scripts 2>/dev/null || npm install) && npm test)

echo "=== 2. Pruebas Android + JaCoCo (Docker) ==="
ANDROID_IMAGE="${ANDROID_IMAGE:-mingc/android-build-box:latest}"
docker pull "$ANDROID_IMAGE"
docker run --rm -v "${ROOT}:/project" -w /project "$ANDROID_IMAGE" \
  bash -lc "chmod +x gradlew && ./gradlew :app:testDebugUnitTest :app:jacocoTestReport --no-daemon"

if [[ -n "${SONAR_TOKEN:-}" ]]; then
  echo "=== 3. SonarQube local ==="
  docker run --rm -v "${ROOT}:/project" -w /project -e SONAR_TOKEN "$ANDROID_IMAGE" \
    bash -lc "chmod +x gradlew && ./gradlew sonar -Dsonar.host.url=${SONAR_HOST_URL:-http://host.docker.internal:9000} -Dsonar.token=${SONAR_TOKEN} -Dsonar.projectKey=happyjump-local --no-daemon"
else
  echo "=== 3. SonarQube omitido (export SONAR_TOKEN) ==="
fi

echo "=== Listo ==="
