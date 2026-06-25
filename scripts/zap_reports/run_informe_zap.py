#!/usr/bin/env python3
"""
Informe de pruebas de seguridad: OWASP ZAP + casos Postman SEC-xxx.
Genera Word y Excel para el entregable 8.

Uso:
  pip install -r requirements.txt
  python run_informe_zap.py

Requisitos:
  1) API en http://localhost:3000
  2) Informe ZAP en zap/zap-report.json (ejecutar antes ..\\run-zap-seguridad.ps1)
"""

from __future__ import annotations

import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(Path(__file__).resolve().parent))
sys.path.insert(0, str(ROOT / "scripts" / "postman_reports"))

from zap_lib import generar_excel, generar_word, cargar_alerts_zap, resumen_zap
from postman_lib import ejecutar_coleccion, imprimir_resumen

COLECCION = ROOT / "docs" / "postman" / "HappyJump-Seguridad.postman_collection.json"
ZAP_JSON = ROOT / "zap" / "zap-report.json"
SALIDA_DIR = Path(__file__).resolve().parent / "reportes"
BASE_URL = "http://localhost:3000"


def main() -> int:
    print("Happy Jump — Informe de seguridad (OWASP ZAP + Postman)")
    print()

    if not ZAP_JSON.exists():
        print(f"AVISO: No existe {ZAP_JSON}")
        print("Ejecuta primero: .\\scripts\\run-zap-seguridad.ps1")
        print("(Se generará el informe solo con pruebas Postman si continúas)\n")

    alerts = cargar_alerts_zap(ZAP_JSON) if ZAP_JSON.exists() else []
    zap_res = resumen_zap(alerts)
    if alerts:
        print(
            f"ZAP: {zap_res['total']} alertas "
            f"(Alta={zap_res['alta']} Media={zap_res['media']} Baja={zap_res['baja']} Info={zap_res['info']})"
        )
    else:
        print("ZAP: sin datos (ejecuta el escaneo Docker)")

    print("\nEjecutando pruebas Postman SEC-001 … SEC-017 …")
    informe_postman = ejecutar_coleccion(
        COLECCION,
        BASE_URL,
        "Pruebas dinámicas de seguridad (Postman)",
        server_root=ROOT / "server",
    )
    codigo = imprimir_resumen(informe_postman)

    SALIDA_DIR.mkdir(parents=True, exist_ok=True)
    docx = SALIDA_DIR / "INFORME_PRUEBAS_SEGURIDAD_ZAP.docx"
    xlsx = SALIDA_DIR / "RESULTADOS_SEGURIDAD_ZAP.xlsx"

    generar_word(informe_postman, alerts, zap_res, docx, BASE_URL)
    generar_excel(informe_postman, alerts, zap_res, xlsx)

    print(f"\nInforme Word:\n  {docx}")
    print(f"Excel:\n  {xlsx}")
    if ZAP_JSON.exists():
        html = ROOT / "zap" / "zap-report.html"
        if html.exists():
            print(f"Reporte ZAP HTML:\n  {html}")
    return codigo


if __name__ == "__main__":
    raise SystemExit(main())
