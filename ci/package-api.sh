#!/usr/bin/env bash
# Empaqueta solo la API Node (sin APK). Uso: ./ci/package-api.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DIST="$ROOT/dist"
mkdir -p "$DIST"

API_TAR="$DIST/happyjump-api.tar.gz"
tar -czf "$API_TAR" \
  -C "$ROOT" \
  --exclude='server/node_modules' \
  --exclude='server/.env' \
  server/package.json \
  server/package-lock.json \
  server/openapi.json \
  server/railway-schema.sql \
  server/schema.sql \
  server/src \
  server/migrations \
  server/scripts/init-railway-db.mjs

echo "$API_TAR"
