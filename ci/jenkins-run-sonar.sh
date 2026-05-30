#!/usr/bin/env bash
# Sonar Gradle dentro de Docker (Jenkins con SonarQube local).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE="${ANDROID_IMAGE:-mingc/android-build-box:latest}"
NETWORK="${DOCKER_NETWORK:-happyjump-ci_default}"

: "${SONAR_HOST_URL:?Falta SONAR_HOST_URL}"
: "${SONAR_TOKEN:?Falta SONAR_TOKEN}"

docker run --rm \
  --network "$NETWORK" \
  -v "${ROOT}:/project" \
  -w /project \
  -e SONAR_TOKEN \
  -e SONAR_HOST_URL \
  "$IMAGE" \
  /bin/bash -lc 'chmod +x gradlew && ./gradlew sonar \
    -Dproject.settings=sonar-project.local.properties \
    -Dsonar.host.url=${SONAR_HOST_URL} \
    -Dsonar.token=${SONAR_TOKEN} \
    --no-daemon -Dorg.gradle.jvmargs=-Xmx2560m'
