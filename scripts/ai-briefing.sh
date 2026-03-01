#!/usr/bin/env bash
set -euo pipefail

ITEM="$1"
if [[ -z "${ITEM:-}" ]]; then
  echo "Usage: ai-briefing.sh <ID (F-001 | T-002 | R-003 | A-004 | B-005)>"
  exit 1
fi

find_item() {
  grep -rl "^# $ITEM" docs work || true
}

FILE=$(find_item)
if [[ -z "$FILE" ]]; then
  echo "❌ Cannot find item $ITEM"
  exit 1
fi

echo "# AI Briefing for $ITEM"
echo
echo "## AI Contract"
sed -n '1,120p' CLAUDE.md
echo

echo "## Work item"
sed -n '1,200p' "$FILE"
echo

echo "## Referenced rules"
grep -o 'R-[0-9]\{3\}' "$FILE" | sort -u | while read -r r; do
  f=$(ls docs/rules/$r-*.md 2>/dev/null || true)
  if [[ -n "$f" ]]; then
    echo
    echo "### $r"
    sed -n '1,200p' "$f"
  fi
done
