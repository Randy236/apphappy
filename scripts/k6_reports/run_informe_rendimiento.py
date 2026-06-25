"""Genera INFORME_PRUEBAS_RENDIMIENTO.docx y METRICAS_RENDIMIENTO.xlsx."""

from __future__ import annotations

import sys
from pathlib import Path

from k6_lib import (
    cargar_servidor_desde_json,
    generar_excel,
    generar_word,
    parsear_log_k6,
)

ROOT = Path(__file__).resolve().parents[2]
REPORTES = Path(__file__).resolve().parent / "reportes"
K6_RESULTS = ROOT / "k6" / "results"


def main() -> int:
    log_path = K6_RESULTS / "performance-latest.txt"
    if not log_path.exists() or log_path.stat().st_size > 500_000:
        fallback = K6_RESULTS / "api-all-20260609-135923.txt"
        if fallback.exists():
            log_path = fallback
            print(f"Usando resultados smoke exitosos: {fallback.name}")
    servidor_path = K6_RESULTS / "servidor-info.json"

    if not log_path.exists():
        print(f"Falta {log_path}. Ejecuta primero: .\\scripts\\generar-informe-rendimiento.ps1")
        return 1

    texto = log_path.read_text(encoding="utf-8", errors="replace")
    metricas = parsear_log_k6(texto)
    servidor = cargar_servidor_desde_json(servidor_path)

    REPORTES.mkdir(parents=True, exist_ok=True)
    word = generar_word(
        servidor,
        metricas,
        REPORTES / "INFORME_PRUEBAS_RENDIMIENTO.docx",
    )
    excel = generar_excel(
        servidor,
        metricas,
        REPORTES / "METRICAS_RENDIMIENTO.xlsx",
    )
    print(f"Word:  {word}")
    print(f"Excel: {excel}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
