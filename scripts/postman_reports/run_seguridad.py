#!/usr/bin/env python3
"""
Ejecuta la colección Postman de SEGURIDAD y genera informe Word.

Uso:
  pip install -r requirements.txt
  python run_seguridad.py

Requisito: API en http://localhost:3000 (cd server && npm start)
"""

from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(Path(__file__).resolve().parent))

from postman_lib import ejecutar_coleccion, generar_word, imprimir_resumen

COLECCION = ROOT / "docs" / "postman" / "HappyJump-Seguridad.postman_collection.json"
SALIDA = Path(__file__).resolve().parent / "reportes" / "INFORME_PRUEBAS_SEGURIDAD.docx"
BASE_URL = "http://localhost:3000"


def main() -> int:
    print("Happy Jump - Pruebas de seguridad (Postman a Python)")
    informe = ejecutar_coleccion(
        COLECCION,
        BASE_URL,
        "Informe de Pruebas de Seguridad de la API",
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
