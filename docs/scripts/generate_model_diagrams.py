"""Regenerate the model UML HTML from its authoritative PlantUML sources."""

import html
import os
from pathlib import Path
import re
import subprocess


DOCS = Path(__file__).resolve().parents[1]
DIAGRAMS = DOCS / "diagrams"
RENDERER = DOCS / "node_modules/@markbind/core/src/plugins/default/plantuml.jar"
VIEWS = [
    ("ModelClassDiagram", "Model contracts and owned data",
     "ModelManager implements Model and references the roster and preferences through their contracts."),
    ("ModelPlayerClassDiagram", "Roster and filtered view",
     "The roster and filtered view reference the same player objects."),
    ("ModelIdentityClassDiagram", "Player identity and tags",
     "Each player references one name, one phone, and zero or more tags."),
    ("ModelAssessmentClassDiagram", "Player selection, rating, remarks, and injury",
     "Each player references one selection status, rating, remark, and injury status."),
]


def render_view(stem, title, description):
    """Render a source with the same PlantUML jar used by the documentation site."""
    source = (DIAGRAMS / f"{stem}.puml").read_text()
    java = str(Path(os.environ["JAVA_HOME"]) / "bin/java") if "JAVA_HOME" in os.environ else "java"
    result = subprocess.run([java, "-jar", str(RENDERER), "-tsvg", "-pipe"],
                            input=source, text=True, cwd=DIAGRAMS, capture_output=True, check=True)
    svg = result.stdout[result.stdout.index("<svg"):]
    svg = re.sub(r"<!--.*?-->", "", svg, flags=re.DOTALL)
    # PlantUML reuses IDs across views; namespace them before embedding together.
    for identifier in set(re.findall(r'\bid="([^"]+)"', svg)):
        svg = svg.replace(f'"{identifier}"', f'"{stem}-{identifier}"')
        svg = svg.replace(f'#{identifier}', f'#{stem}-{identifier}')
    width = re.search(r'viewBox="[\d.]+ [\d.]+ ([\d.]+)', svg).group(1)
    svg = svg.replace('<svg ', f'<svg role="img" aria-labelledby="{stem}-title {stem}-desc" ', 1)
    svg = svg.replace('style="', f'style="min-width:{width}px;', 1)
    opening = svg.index('>') + 1
    svg = (svg[:opening] + f'<title id="{stem}-title">{html.escape(title)}</title>'
           + f'<desc id="{stem}-desc">{html.escape(description)}</desc>' + svg[opening:])
    return (f'<section><h2>{title}</h2><div class="diagram-container">{svg}</div>'
            f'<details><summary>Editable PlantUML source: {stem}.puml</summary>'
            f'<pre contenteditable="true" spellcheck="false" aria-label="{title} source">'
            f'{html.escape(source)}</pre></details></section>')


def generate():
    """Write the self-contained HTML only after every source renders successfully."""
    views = "\n".join(render_view(*view) for view in VIEWS)
    document = '''<!doctype html>
<html lang="en"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>CouchCoach model class diagrams</title>
<style>
* { box-sizing: border-box; }
body { margin: 0; padding: 24px; background: #f5f5f5; color: #2d3142;
       font: 14px/1.6 system-ui, sans-serif; }
main { max-width: 1040px; min-width: 0; margin: auto; }
h1 { font: 32px Georgia, serif; } h2 { margin-top: 32px; font-size: 18px; }
p { max-width: 80ch; } .diagram-container { overflow-x: auto; width: 100%; }
svg { display: block; max-width: none; }
details { margin: 12px 0; } pre { overflow-x: auto; padding: 12px; border: 1px solid #bfc0c0; }
footer { margin-top: 32px; border-top: 1px solid #bfc0c0; }
@media print { body { padding: 0; } details { display: none; } section { break-inside: avoid; } }
</style></head><body><main>
<p>CouchCoach · UML class diagrams · v1.4 model</p><h1>The model, in four views</h1>
<p>Repeated class names refer to the same classes across views. Diagrams retain their native
text size; scroll each figure horizontally on narrow screens. Expand a source below to edit
or copy it, then save it to the matching .puml file and regenerate this HTML.</p>
'''
    document += views
    document += '''<footer><h2>Shared notation</h2>
<p>Dashed line with a hollow triangle: interface realization. Solid line with a filled diamond
at the owner: composition. Solid line with an open arrowhead: navigable association.
Dashed line with an open arrowhead: dependency. Multiplicities apply at each association end.
Only these four relationship types are used.</p>
<p>PlantUML is the diagram source. Regenerate with
<code>python3 docs/scripts/generate_model_diagrams.py</code> from the repository root.
Changes to source text in this page are local edits; copy them to the .puml files to keep them.</p>
</footer></main></body></html>
'''
    (DIAGRAMS / "ModelClassDiagram.html").write_text(document)


if __name__ == "__main__":
    generate()
