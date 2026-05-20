#!/usr/bin/env bash
# Happy Jump — empaqueta API (Node) + APK Android para deploy por scripts.
# Uso: desde la raíz del repo → ./ci/build-production.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
DIST="$ROOT/dist"
ANDROID_IMAGE="${ANDROID_IMAGE:-mingc/android-build-box:latest}"
BUILD_ID="${BUILD_ID:-$(date +%Y%m%d-%H%M%S)}"

rm -rf "$DIST"
mkdir -p "$DIST"

echo "=== API: empaquetar fuentes (sin node_modules) ==="
API_TAR="$DIST/happyjump-api.tar.gz"
tar -czf "$API_TAR" \
  -C "$ROOT" \
  --exclude='server/node_modules' \
  --exclude='server/.env' \
  server/package.json \
  server/package-lock.json \
  server/src \
  server/migrations \
  server/schema.sql

echo "=== Android: pruebas unitarias + APK release ==="
if command -v docker >/dev/null 2>&1 && [[ "${ANDROID_BUILD_DOCKER:-1}" == "1" ]]; then
  docker pull "$ANDROID_IMAGE" >/dev/null 2>&1 || true
  docker run --rm \
    -v "${ROOT}:/project" \
    -w /project \
      -e GRADLE_USER_HOME=/project/.gradle-ci \
      -e TMPDIR=/project/tmp \
    "$ANDROID_IMAGE" \
    bash -lc "chmod +x gradlew && ./gradlew :app:assembleRelease --no-daemon -Dorg.gradle.jvmargs='-Xmx2560m'"
else
  chmod +x gradlew
  ./gradlew :app:assembleRelease --no-daemon
fi

APK_SRC="$ROOT/app/build/outputs/apk/release/app-release-unsigned.apk"
if [[ ! -f "$APK_SRC" ]]; then
  echo "No se encontró APK en $APK_SRC" >&2
  exit 1
fi

cp "$APK_SRC" "$DIST/happyjump-app-release.apk"
cp "$APK_SRC" "$DIST/happyjump-app-${BUILD_ID}.apk"

cat > "$DIST/build-info.json" <<EOF
{
  "proyecto": "Happy Jump",
  "build": "${BUILD_ID}",
  "fecha": "$(date -Iseconds 2>/dev/null || date)",
  "artefactos": {
    "api": "happyjump-api.tar.gz",
    "apk": "happyjump-app-release.apk"
  }
}
EOF

echo "Build finalizado:"
echo "  $API_TAR"
echo "  $DIST/happyjump-app-release.apk"
echo "  $DIST/build-info.json"
