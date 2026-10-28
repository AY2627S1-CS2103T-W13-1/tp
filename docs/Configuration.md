---
  layout: default.md
  title: "Configuration guide"
---

# Configuration guide

CouchCoach uses fixed file paths relative to the terminal's working directory:

| File | Purpose |
| --- | --- |
| `data/addressbook.json` | The whole player roster, written after data-changing commands and explicit `save` |
| `preferences.json` | Window size and position, written at startup and shutdown |
| `addressbook.log.*` | Rotating diagnostic logs |

Launch from the same writable folder each time. See [Saving the data](UserGuide.md#saving-the-data) and [Editing the data file](UserGuide.md#editing-the-data-file) for recovery and backup instructions. To reset the window position, close the app and delete only `preferences.json`.

There is no supported `config.json` file or command-line option for changing these paths. The defaults are defined in `MainApp`; logging level is controlled by `LogsCenter.LOG_LEVEL` in source. See the [Logging guide](Logging.md).
