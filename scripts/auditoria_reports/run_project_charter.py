"""Genera Project Charter Auditoría SDLC — Happy Jump."""

import sys
from pathlib import Path

from project_charter_lib import generar_charter

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "entregableunidad" / "entregable-auditoria" / "PROJECT_CHARTER_AUDITORIA_SDLC_HAPPY_JUMP.docx"


def main() -> int:
    path = generar_charter(OUT)
    print(f"Word generado: {path}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
