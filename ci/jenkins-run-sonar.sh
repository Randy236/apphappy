#!/usr/bin/env bash
set -euo pipefail

SCRIPT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE="${ANDROID_IMAGE:-mingc/android-build-box:latest}"
NETWORK="${DOCKER_NETWORK:-happyjump-ci_default}"

: "${SONAR_HOST_URL:?Falta SONAR_HOST_URL}"
: "${SONAR_TOKEN:?Falta SONAR_TOKEN}"

gradlew_ready() {
  [[ -n "${WORKSPACE:-}" && -f "${WORKSPACE}/gradlew" ]] && return 0
  [[ -f /workspace/apphappy/gradlew ]] && return 0
  return 1
}

resolve_mount_dir() {
  if [[ -n "${DOCKER_MOUNT_DIR:-}" ]]; then
    echo "${DOCKER_MOUNT_DIR//\\//}"
    return
  fi
  if [[ -n "${WORKSPACE:-}" && -f "${WORKSPACE}/gradlew" ]]; then
    local job host_root
    job="$(basename "${WORKSPACE}")"
    host_root="${JENKINS_HOST_WORKSPACE_ROOT:-/var/jenkins_home/workspace}"
    echo "${host_root//\\//}/${job}"
    return
  fi
  echo "${DOCKER_REPO_PATH:-/workspace/apphappy}"
}

if ! gradlew_ready; then
  echo "No hay gradlew para Sonar" >&2
  exit 1
fi

MOUNT_DIR="$(resolve_mount_dir)"
MOUNT_DIR="${MOUNT_DIR//\\//}"

echo "=== Sonar Docker mount: ${MOUNT_DIR} ==="

docker run --rm \
  --network "$NETWORK" \
  -v "${MOUNT_DIR}:/project" \
  -w /project \
  -e SONAR_TOKEN \
  -e SONAR_HOST_URL \
  "$IMAGE" \
  /bin/bash -lc 'sed -i "s/\r$//" gradlew 2>/dev/null || true; chmod +x gradlew && ./gradlew sonar \
    -Dproject.settings=sonar-project.local.properties \
    -Dsonar.host.url=${SONAR_HOST_URL} \
    -Dsonar.token=${SONAR_TOKEN} \
    --no-daemon -Dorg.gradle.jvmargs=-Xmx2560m'
