---
  layout: default.md
  title: "Documentation guide"
  pageNav: 3
---

# Documentation Guide

* We use [**MarkBind**](https://markbind.org/) to manage documentation.
* The `docs/` folder contains the source files for the documentation website.
* To learn how set it up and maintain the project website, follow the guide [[se-edu/guides] Working with Forked MarkBind sites](https://se-education.org/guides/tutorials/markbind-forked-sites.html) for project documentation.

## Building and previewing the site

The repository uses MarkBind 7.2.1 for both local builds and deployment. Install a supported Node.js version and npm, Java 25 for PlantUML, and Graphviz (`dot`) for diagrams that use it. From the repository root:

```bash
npm ci --prefix docs
npm run build --prefix docs
npm run serve --prefix docs
```

`build` generates `docs/_site`; `serve` opens a local preview. Do not commit `_site` or `node_modules`. Before opening a documentation PR, inspect the generated UG, DG, navigation, tables, screenshot and diagrams, including narrow-screen and print layouts. Deployment runs on `master`; building a PR locally does not publish it. The inherited `team/johndoe.md` portfolio example is excluded from published pages; it is a template, not a team member or a record of implemented features.

Edit PlantUML sources in `docs/diagrams`, using the existing component colors in `style.puml`. The site renders `<puml>` elements at build time. If a diagram also has a generated HTML view, refresh it after changing its source:

```bash
python3 docs/scripts/generate_model_diagrams.py
python3 docs/scripts/generate_rate_sequence.py
python3 docs/scripts/generate_rate_sequence.py --check
```

Keep generated HTML and its source in the same commit. Verify internal links against built `.html` pages, since heading punctuation affects fragment IDs. Use the browser's Print preview for PDF output; check that diagrams and wide tables are legible before sharing.

## Style guidance


* Follow the [**_Google developer documentation style guide_**](https://developers.google.com/style).
* Also relevant is the [_se-edu/guides **Markdown coding standard**_](https://se-education.org/guides/conventions/markdown.html).


**Converting to PDF**

* See the guide [_se-edu/guides **Saving web documents as PDF files**_](https://se-education.org/guides/tutorials/savingPdf.html).
