"""Generate the default-style HTML sequence from its single editable PlantUML source."""

from html import escape
from pathlib import Path
import re
import sys

DIAGRAMS = Path(__file__).resolve().parents[1] / "diagrams"
LABELS = {"parseCommand": "PARSE COMMAND", "execute": "EXECUTE", "getFilteredPersonList": "GET LIST",
          "setPerson": "SET PLAYER", "getAddressBook": "GET TEAM", "saveAddressBook": "SAVE TEAM",
          "c": "COMMAND", "displayed": "PLAYERS", "result": "RESULT", "addressBook": "TEAM", "": "RETURN"}


def generate():
    """Read participants, calls, returns, activations and notes directly from PlantUML."""
    source = (DIAGRAMS / "RateSequenceDiagram.puml").read_text()
    names = re.findall(r'^participant "[^"]+" as (\w+)', source, re.M)
    if len(names) != 5:
        raise ValueError("The doc-inline sequence layout supports exactly five participants.")
    centers = dict(zip(names, [96, 288, 480, 672, 864]))
    body, messages, masks, nodes, notes = [], [], [], [], []
    active, y, count = {}, 96, 0
    for name, x in centers.items():
        body.append(f'<line x1="{x}" y1="88" x2="{x}" y2="512" stroke="rgba(45,49,66,0.20)" stroke-dasharray="3,3"/>')
        width = 160 if len(name) > 14 else 112
        nodes.append(f'<rect x="{x-width//2}" y="40" width="{width}" height="48" rx="6" fill="#ffffff" stroke="#2d3142"/><text x="{x}" y="69" class="name" text-anchor="middle">{escape(name)}</text>')
    lines = iter(source.splitlines())
    for line in lines:
        if line.startswith("note over "):
            note = []
            for text in lines:
                if text == "end note":
                    break
                note.append(text.strip())
            notes.append('<p class="scope">' + escape(" ".join(note)) + '</p>')
        elif line.startswith("activate "):
            name = line.split()[1]
            active[name] = y
        elif line.startswith("deactivate "):
            name = line.split()[1]
            start = active.pop(name)
            x = centers[name]
            nodes.append(f'<rect x="{x-4}" y="{start}" width="8" height="{max(8,y-start)}" fill="#ececec" stroke="#4f5d75" stroke-width="0.8"/>')
        else:
            match = re.fullmatch(r'(\w+) (--?>) (\w+)(?: : (.*))?', line)
            if not match:
                continue
            first, arrow, second, text = match.groups()
            y, count = y + 32, count + 1
            if count > 12:
                raise ValueError("Split the sequence: the default skill budget allows twelve messages.")
            label = LABELS[(text or "").split("(")[0]]
            a, b = centers[first], centers[second]
            offset = 4 if a < b else -4
            dash = "5,4" if arrow == "-->" else "none"
            messages.append(f'<line x1="{a+offset}" y1="{y}" x2="{b-offset}" y2="{y}" stroke="#4f5d75" stroke-dasharray="{dash}" marker-end="url(#arrow)"/>')
            # Use a clear gap between lifelines even for messages spanning several participants.
            cx = (min(a, b) + 96) if abs(a-b) <= 384 else (max(a, b) - 96)
            width = ((int(len(label)*8*0.62) + 19)//4)*4
            masks.append(f'<rect x="{cx-width//2}" y="{y-20}" width="{width}" height="12" fill="#f5f5f5"/><text x="{cx}" y="{y-11}" class="label" text-anchor="middle">{label}</text>')
    if active:
        raise ValueError("Every activation must close before generating the HTML.")
    legend = '<line x1="40" y1="540" x2="920" y2="540" stroke="rgba(45,49,66,0.12)"/>'
    legend += '<text x="64" y="575" class="label">SOLID ARROW: CALL · DASHED ARROW: RETURN · NARROW BAR: ACTIVE CALL</text>'
    notes.insert(0, '<p class="scope">Successful rate 1 8. The external caller and return are shown in the PlantUML view; this overview retains five participants.</p>')
    template = (DIAGRAMS / "RateSequenceDiagram.template.html").read_text()
    return template.replace("{{diagram}}", "\n".join(body+messages+masks+nodes+[legend])).replace("{{notes}}", "\n".join(notes))


if __name__ == "__main__":
    target = DIAGRAMS / "RateSequenceDiagram.html"
    document = generate()
    if "--check" in sys.argv:
        if target.read_text() != document:
            raise SystemExit("Rate sequence HTML is stale; run python3 docs/scripts/generate_rate_sequence.py")
    else:
        target.write_text(document)
