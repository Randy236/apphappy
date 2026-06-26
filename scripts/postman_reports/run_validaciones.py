#!/usr/bin/env python3
"""
Ejecuta la colección Postman de VALIDACIÓN de parámetros y genera informe Word.

Uso:
  pip install -r requirements.txt
  python run_validaciones.py

Requisito: API en https://happyjump.sorbits.site (producción en la nube)
"""

from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(Path(__file__).resolve().parent))

from postman_lib import ejecutar_coleccion, generar_word, imprimir_resumen

COLECCION = ROOT / "docs" / "postman" / "HappyJump-Validaciones.postman_collection.json"
SALIDA = Path(__file__).resolve().parent / "reportes" / "INFORME_VALIDACION_PARAMETROS.docx"
BASE_URL = "https://happyjump.sorbits.site"


def main() -> int:
    print("Happy Jump - Pruebas de validacion de parametros (Postman a Python)")
    informe = ejecutar_coleccion(
        COLECCION,
        BASE_URL,
        "Informe de Pruebas de Validación de Parámetros",
        server_root=ROOT / "server",
    )
    codigo = imprimir_resumen(informe)
    if informe.error_conexion:
        return codigo
    ruta = generar_word(informe, SALIDA)
    print(f"\nInforme Word generado:\n  {ruta}")
    return codigo


if __name__ == "__main__":
    raise SystemExit(main())
