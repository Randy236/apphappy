"""Genera Checklist SDLC Audit — Happy Jump."""

import sys
from pathlib import Path

from checklist_sdlc_lib import _items, generar_excel, generar_word

ROOT = Path(__file__).resolve().parents[2]
OUT_DIR = ROOT / "entregableunidad" / "entregable-auditoria"


def main() -> int:
    items = _items()
    word = generar_word(items, OUT_DIR / "CHECKLIST_SDLC_AUDIT_HAPPY_JUMP.docx")
    excel = generar_excel(items, OUT_DIR / "CHECKLIST_SDLC_AUDIT_HAPPY_JUMP.xlsx")
    ok = sum(1 for i in items if i.cumple)
    print(f"Word:  {word}")
    print(f"Excel: {excel}")
    print(f"Cumplimiento: {ok}/{len(items)} ({100*ok/len(items):.1f}%)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
