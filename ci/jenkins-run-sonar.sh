#!/usr/bin/env bash
set -euo pipefail

SCRIPT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE="${ANDROID_IMAGE:-mingc/android-build-box:latest}"
NETWORK="${DOCKER_NETWORK:-happyjump-ci_default}"

: "${SONAR_HOST_URL:?Falta SONAR_HOST_URL}"
: "${SONAR_TOKEN:?Falta SONAR_TOKEN}"

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
    echo "${host_root}/${job}"
    return
  fi
  echo "${DOCKER_REPO_PATH:-$SCRIPT_ROOT}"
}

MOUNT_DIR="$(resolve_mount_dir)"
MOUNT_DIR="${MOUNT_DIR//\\//}"

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
