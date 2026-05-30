#!/usr/bin/env bash
# Gradle en contenedor Android. Jenkins en Docker: montar ruta Windows visible por el daemon.
set -euo pipefail

SCRIPT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE="${ANDROID_IMAGE:-mingc/android-build-box:latest}"

# Comprueba gradlew en rutas visibles DENTRO del contenedor Jenkins (Linux)
gradlew_ready() {
  [[ -n "${WORKSPACE:-}" && -f "${WORKSPACE}/gradlew" ]] && return 0
  [[ -f /workspace/apphappy/gradlew ]] && return 0
  [[ -f "${SCRIPT_ROOT}/gradlew" ]] && return 0
  return 1
}

# Ruta Windows para "docker run -v" (el daemon no ve /var/jenkins_home/...)
resolve_mount_dir() {
  if [[ -n "${DOCKER_MOUNT_DIR:-}" ]]; then
    echo "${DOCKER_MOUNT_DIR//\\//}"
    return
  fi
  if [[ -n "${WORKSPACE:-}" && -f "${WORKSPACE}/gradlew" ]]; then
    local job host_root
    job="$(basename "${WORKSPACE}")"
    host_root="${JENKINS_HOST_WORKSPACE_ROOT:-E:/happyjump-ci/data/jenkins_home/workspace}"
    echo "${host_root//\\//}/${job}"
    return
  fi
  if [[ -f /workspace/apphappy/gradlew ]]; then
    echo "${DOCKER_REPO_PATH:-D:/apphappy-full}"
    return
  fi
  echo "${DOCKER_REPO_PATH:-D:/apphappy-full}"
}

if ! gradlew_ready; then
  echo "No hay gradlew en WORKSPACE ni /workspace/apphappy" >&2
  echo "WORKSPACE=${WORKSPACE:-<vacío>}" >&2
  exit 1
fi

MOUNT_DIR="$(resolve_mount_dir)"
MOUNT_DIR="${MOUNT_DIR//\\//}"

echo "=== Gradle Docker mount: ${MOUNT_DIR} ==="
echo "=== WORKSPACE: ${WORKSPACE:-<vacío>} ==="

docker pull "$IMAGE" >/dev/null 2>&1 || true

docker run --rm \
  -v "${MOUNT_DIR}:/project" \
  -w /project \
  -e GRADLE_USER_HOME=/project/.gradle-jenkins \
  "$IMAGE" \
  /bin/bash -lc "sed -i 's/\r$//' gradlew 2>/dev/null || true; chmod +x gradlew && ./gradlew $* --no-daemon -Dorg.gradle.jvmargs=-Xmx2560m"
