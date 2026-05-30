#!/usr/bin/env bash
# Ejecuta Gradle dentro de mingc/android-build-box (Jenkins / local).
# Uso: ./ci/jenkins-run-gradle.sh :app:testDebugUnitTest :app:jacocoTestReport
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE="${ANDROID_IMAGE:-mingc/android-build-box:latest}"
GRADLE_DIR="${GRADLE_USER_HOME:-$ROOT/.gradle-jenkins}"

if [[ ! -f "$ROOT/gradlew" ]]; then
  echo "No existe $ROOT/gradlew — revisa el checkout del repo." >&2
  exit 1
fi

docker pull "$IMAGE" >/dev/null 2>&1 || true

docker run --rm \
  -v "${ROOT}:/project" \
  -w /project \
  -e GRADLE_USER_HOME=/project/.gradle-jenkins \
  "$IMAGE" \
  /bin/bash -lc "chmod +x gradlew && ./gradlew $* --no-daemon -Dorg.gradle.jvmargs=-Xmx2560m"
