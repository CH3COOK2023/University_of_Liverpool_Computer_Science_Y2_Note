#!/bin/bash
# Extract text from all lecture PDFs into one combined txt file
PPT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUTPUT="$PPT_DIR/all_lectures_combined.txt"
cd "$PPT_DIR"
> "$OUTPUT"

for pdf in *.pdf; do
    name="${pdf%.pdf}"
    echo "Processing: $name"
    {
        echo "============================================================"
        echo "  $name"
        echo "============================================================"
        echo ""
    } >> "$OUTPUT"
    pdftotext -layout "$pdf" - >> "$OUTPUT"
    printf "\n\n" >> "$OUTPUT"
done

echo ""
echo "Done! Output: $OUTPUT"
echo "File size: $(wc -c < "$OUTPUT") bytes"
echo "Line count: $(wc -l < "$OUTPUT") lines"
