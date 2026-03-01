#!/usr/bin/env bash
set -euo pipefail

mkdir -p generated

OUT=generated/STATUS.md
echo "# Project Status" > "$OUT"
echo >> "$OUT"

echo "## Features" >> "$OUT"
ls work/features 2>/dev/null | sed 's/^/- /' >> "$OUT"
echo >> "$OUT"

echo "## Tasks" >> "$OUT"
ls work/tasks 2>/dev/null | sed 's/^/- /' >> "$OUT"
echo >> "$OUT"

echo "## Bugs" >> "$OUT"
ls work/bugs 2>/dev/null | sed 's/^/- /' >> "$OUT"
echo >> "$OUT"

echo "## Rules" >> "$OUT"
ls docs/rules 2>/dev/null | sed 's/^/- /' >> "$OUT"

echo "Generated at $(date)" >> "$OUT"
