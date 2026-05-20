#!/usr/bin/env bash
# Happy Jump — deploy local por carpetas (API Node + APK), con rollback.
# Variables:
#   HAPPYJUMP_DEPLOY_BASE  (default: $HOME/servers/happyjump)
#   DEPLOY_TOKEN           (opcional; si está definido, debe coincidir con credencial Jenkins)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DIST="$ROOT/dist"
BASE="${HAPPYJUMP_DEPLOY_BASE:-$HOME/servers/happyjump}"
API_CURRENT="$BASE/api/current"
API_RELEASES="$BASE/api/releases"
APP_CURRENT="$BASE/app/current"
APP_RELEASES="$BASE/app/releases"
ROLLBACK="$BASE/rollback"
LOG="$BASE/logs/deploy.log"
BUILD_ID="${BUILD_NUMBER:-${BUILD_ID:-local}}"

mkdir -p "$API_CURRENT" "$API_RELEASES" "$APP_CURRENT" "$APP_RELEASES" "$ROLLBACK" "$(dirname "$LOG")"

if [[ -n "${EXPECTED_DEPLOY_TOKEN:-}" ]]; then
  if [[ "${DEPLOY_TOKEN:-}" != "$EXPECTED_DEPLOY_TOKEN" ]]; then
    echo "deploy-token inválido o ausente" >&2
    exit 1
  fi
fi

API_PKG="$DIST/happyjump-api.tar.gz"
APK_PKG="$DIST/happyjump-app-release.apk"

if [[ ! -f "$API_PKG" ]]; then
  echo "Falta $API_PKG — ejecuta primero ./ci/build-production.sh" >&2
  exit 1
fi
if [[ ! -f "$APK_PKG" ]]; then
  echo "Falta $APK_PKG — ejecuta primero ./ci/build-production.sh" >&2
  exit 1
fi

log() {
  echo "[$(date -Iseconds 2>/dev/null || date)] $*" | tee -a "$LOG"
}

log "=== Deploy Happy Jump (build ${BUILD_ID}) ==="

# --- Rollback API ---
if [[ -f "$API_CURRENT/package.json" ]]; then
  ROLLBACK_API="$ROLLBACK/api_rollback_$(date +%Y%m%d_%H%M%S).tar.gz"
  tar -czf "$ROLLBACK_API" -C "$API_CURRENT" .
  log "Rollback API guardado: $ROLLBACK_API"
fi

# --- Publicar API ---
RELEASE_DIR="$API_RELEASES/${BUILD_ID}"
rm -rf "$RELEASE_DIR"
mkdir -p "$RELEASE_DIR"
tar -xzf "$API_PKG" -C "$RELEASE_DIR"
(
  cd "$RELEASE_DIR/server"
  if command -v npm >/dev/null 2>&1; then
    npm ci --omit=dev --ignore-scripts 2>/dev/null || npm install --omit=dev
  else
    log "AVISO: npm no disponible en el agente; instala dependencias en el servidor destino."
  fi
)
rm -rf "$API_CURRENT"
mkdir -p "$API_CURRENT"
cp -a "$RELEASE_DIR/server/." "$API_CURRENT/"
log "API publicada en $API_CURRENT"

# --- Rollback APK ---
if [[ -f "$APP_CURRENT/happyjump-app-release.apk" ]]; then
  ROLLBACK_APK="$ROLLBACK/app_rollback_$(date +%Y%m%d_%H%M%S).apk"
  cp "$APP_CURRENT/happyjump-app-release.apk" "$ROLLBACK_APK"
  log "Rollback APK guardado: $ROLLBACK_APK"
fi

# --- Publicar APK ---
APK_RELEASE="$APP_RELEASES/happyjump_${BUILD_ID}.apk"
cp "$APK_PKG" "$APK_RELEASE"
mkdir -p "$APP_CURRENT"
cp "$APK_PKG" "$APP_CURRENT/happyjump-app-release.apk"
if [[ -f "$DIST/build-info.json" ]]; then
  cp "$DIST/build-info.json" "$APP_CURRENT/build-info.json"
fi
log "APK publicado en $APP_CURRENT/happyjump-app-release.apk"
log "Histórico: $APK_RELEASE"

log "Deploy exitoso → $BASE"
log "Log: $LOG"
