#!/usr/bin/env bash
# Gradle en contenedor Android. Jenkins en Docker debe montar ruta del HOST (Windows).
set -euo pipefail

SCRIPT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE="${ANDROID_IMAGE:-mingc/android-build-box:latest}"

# Jenkins exporta WORKSPACE=/var/jenkins_home/workspace/<job>
# El daemon Docker (socket) no ve esa ruta: hay que usar el volumen en E: o D:
resolve_mount_dir() {
  if [[ -n "${DOCKER_MOUNT_DIR:-}" && -f "${DOCKER_MOUNT_DIR}/gradlew" ]]; then
    echo "${DOCKER_MOUNT_DIR}"
    return
  fi
  if [[ -n "${WORKSPACE:-}" && -f "${WORKSPACE}/gradlew" ]]; then
    local job
    job="$(basename "${WORKSPACE}")"
    local host_root="${JENKINS_HOST_WORKSPACE_ROOT:-E:/happyjump-ci/data/jenkins_home/workspace}"
    host_root="${host_root//\\//}"
    local host_ws="${host_root}/${job}"
    if [[ -f "${host_ws}/gradlew" ]]; then
      echo "${host_ws}"
      return
    fi
  fi
  if [[ -f /workspace/apphappy/gradlew ]]; then
    echo "${DOCKER_REPO_PATH:-/workspace/apphappy}"
    return
  fi
  echo "${DOCKER_REPO_PATH:-$SCRIPT_ROOT}"
}

MOUNT_DIR="$(resolve_mount_dir)"
MOUNT_DIR="${MOUNT_DIR//\\//}"

if [[ ! -f "${MOUNT_DIR}/gradlew" ]]; then
  echo "No existe gradlew en ${MOUNT_DIR}" >&2
  echo "WORKSPACE=${WORKSPACE:-<vacío>}" >&2
  ls -la "${MOUNT_DIR}" 2>/dev/null | head -15 >&2 || true
  exit 1
fi

echo "=== Gradle Docker mount: ${MOUNT_DIR} ==="

docker pull "$IMAGE" >/dev/null 2>&1 || true

docker run --rm \
  -v "${MOUNT_DIR}:/project" \
  -w /project \
  -e GRADLE_USER_HOME=/project/.gradle-jenkins \
  "$IMAGE" \
  /bin/bash -lc "sed -i 's/\r$//' gradlew 2>/dev/null || true; chmod +x gradlew && ./gradlew $* --no-daemon -Dorg.gradle.jvmargs=-Xmx2560m"
