#!/usr/bin/env sh
# Actualiza la API en el servidor de producción: git pull + npm ci + reinicio.
# Se invoca desde POST /internal/deploy (webhook) o manualmente en el servidor.
set -eu

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="${HAPPYJUMP_REPO_ROOT:-$(cd "$SCRIPT_DIR/../.." && pwd)}"
BRANCH="${DEPLOY_GIT_BRANCH:-main}"

echo "[deploy] repo=$REPO_ROOT branch=$BRANCH"

if ! command -v git >/dev/null 2>&1; then
  echo "[deploy] ERROR: git no disponible en el servidor" >&2
  exit 1
fi

cd "$REPO_ROOT"
git fetch origin "$BRANCH"
git reset --hard "origin/$BRANCH"

cd "$REPO_ROOT/server"
npm ci --omit=dev --ignore-scripts 2>/dev/null || npm install --omit=dev

if command -v pm2 >/dev/null 2>&1; then
  pm2 restart happyjump-api 2>/dev/null || pm2 start src/index.js --name happyjump-api
else
  pkill -f 'node src/index.js' 2>/dev/null || true
  nohup node src/index.js >> happyjump-api.log 2>&1 &
fi

echo "[deploy] OK"
