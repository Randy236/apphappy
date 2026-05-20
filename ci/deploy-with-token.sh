#!/usr/bin/env bash
# Wrapper para Jenkins cuando configures credencial deploy-token.
# Uso en un stage extra o reemplazando el stage Deploy del Jenkinsfile:
#   withCredentials([string(credentialsId: 'deploy-token', variable: 'DEPLOY_TOKEN')]) {
#     sh './ci/deploy-with-token.sh'
#   }
set -euo pipefail
export EXPECTED_DEPLOY_TOKEN="${DEPLOY_TOKEN:?Falta DEPLOY_TOKEN}"
exec "$(dirname "$0")/deploy-server.sh"
