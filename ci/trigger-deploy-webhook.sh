#!/usr/bin/env bash
# Dispara el deploy en producción vía webhook HTTPS.
# Secrets GitHub: HAPPYJUMP_DEPLOY_TOKEN
# Opcional: HAPPYJUMP_PUBLIC_URL (default https://happyjump.sorbits.site)
set -euo pipefail

PUBLIC_URL="${HAPPYJUMP_PUBLIC_URL:-https://happyjump.sorbits.site}"
DEPLOY_URL="${HAPPYJUMP_DEPLOY_URL:-${PUBLIC_URL%/}/internal/deploy}"
: "${HAPPYJUMP_DEPLOY_TOKEN:?Define HAPPYJUMP_DEPLOY_TOKEN}"

echo "=== Webhook deploy ? $DEPLOY_URL ==="
curl -fsS -X POST \
  -H "Authorization: Bearer ${HAPPYJUMP_DEPLOY_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{\"ref\":\"${GITHUB_REF_NAME:-main}\",\"sha\":\"${GITHUB_SHA:-manual}\"}" \
  "$DEPLOY_URL"

echo ""
echo "Webhook aceptado (202). Esperando reinicio..."
sleep 8
curl -fsS "${PUBLIC_URL%/}/health"
echo ""
echo "=== Health OK ==="
