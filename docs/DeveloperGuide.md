---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# CouchCoach Developer Guide

## **Value proposition**

CouchCoach helps coaches of any sports team keep player details and observations together while comparing players and deciding who to keep or cut. Fast name lookup, custom tags, ratings and selection views support that work through typed commands and a desktop player list. See [Product scope](#product-scope) for the intended users and boundaries.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* CouchCoach adapts the application structure, code and documentation of [SE-EDU AddressBook Level 3](https://github.com/se-edu/addressbook-level3).
* The application uses [JavaFX](https://openjfx.io/) for its desktop interface and [Jackson](https://github.com/FasterXML/jackson) for JSON storage. Tests use [JUnit 5](https://junit.org/junit5/).
* The documentation site uses [MarkBind](https://markbind.org/) and its [PlantUML](https://plantuml.com/) support for diagrams.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="560" alt="CouchCoach component dependencies" />

CouchCoach retains AB3's component boundaries. [`Main`](https://github.com/AY2627S1-CS2103T-W13-1/tp/blob/master/src/main/java/seedu/address/Main.java)
launches JavaFX's [`MainApp`](https://github.com/AY2627S1-CS2103T-W13-1/tp/blob/master/src/main/java/seedu/address/MainApp.java),
which loads preferences and player data, constructs `ModelManager`, `StorageManager`,
`LogicManager` and `UiManager`, and connects them through their interfaces.
On shutdown, `MainApp#stop()` saves user preferences. Player data is saved after a
successful data-changing command, rather than on exit.

* [UI](#ui-component) accepts coach commands and displays the observable player view and feedback.
* [Logic](#logic-component) parses commands, executes them against `Model`, and decides whether to save.
* [Model](#model-component) holds the roster, immutable player values, preferences and the current display view.
* [Storage](#storage-component) reads/writes roster and preference JSON through separate storage classes.
* [Commons](#common-classes) supplies shared utilities, indices, logging and exceptions; it is not a fifth command-processing stage.

`UI` calls the `Logic` interface and displays model types such as `Person`; it does
not write player files. `LogicManager` holds `Model` and `Storage` references.
Storage depends on model contracts to convert and persist data. Model does not
reference the UI, Logic or Storage components.

The existing sequence illustrates the **mutating** path for `delete 1`:

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="650" alt="Delete command updates Model and then saves through Storage" />

For view commands (`list`, `find`, `filter`, `sort`, `squad`) and `help`/`exit`,
`LogicManager` returns without saving player data. A read-only command can still
change a filter or comparator in memory. Parse/command errors return before saving;
an I/O failure after mutation leaves the change in memory and returns the
applied command feedback with a save warning. The explicit `save` command retries
persistence without repeating the mutation. The UI observes changes to the displayed list,
rather than needing a saved-file reload after each command.

Each component defines an interface and a manager implementation. The following
partial diagram retains the shared contracts without expanding feature internals:

<puml src="diagrams/ComponentManagers.puml" width="420" alt="LogicManager depends on Model and Storage interfaces" />

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-W13-1/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

See the [player-card detail](diagrams/PlayerCardClassDiagram.html) for the implemented
US21 remarks and US13 injury badge. Empty remarks and fit-player injury badges are
hidden and unmanaged, so they do not leave blank rows.

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-W13-1/tp/blob/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-W13-1/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java).

`HelpPanel` is embedded in the player-list area of `MainWindow`; it is not a separate dialog. `help`, the Help menu and F1 show the same summary. The underlying player view remains active. Any successful command other than `help` restores the player panel, including a `save` retry; a rejected command leaves help visible. `CommandBox` uses a `TextFormatter` to replace tabs with ordinary spaces before `TextField` strips control characters. This preserves pasted command boundaries, including tabs inside a value, without changing Tab-key focus traversal. It clears after a returned `CommandResult`, including save warnings, but retains invalid input when parsing or execution throws.

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API:** [`Logic.java`](https://github.com/AY2627S1-CS2103T-W13-1/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

<puml src="diagrams/LogicClassDiagram.puml" width="720" alt="LogicManager coordinates parsing, command execution, Model and Storage" />

`LogicManager#execute(commandText)` performs the following steps:

1. `AddressBookParser#parseCommand` identifies the lowercase command word. Commands needing arguments delegate to their command parser; parameterless commands such as `list`, `squad`, `help` and `exit` are constructed directly. `clear` and `save` also check that their arguments are blank.
2. The parser returns a concrete `Command`. Shared `ParserUtil` operations validate values and map one-based input to an `Index`; they do not resolve which player occupies that index.
3. An index-based `Command#execute(model)` resolves the index against `Model#getFilteredPersonList()`, which is the **displayed sorted view**, despite the historical method name. A mutation replaces a `Person` or changes the roster; a view command changes a predicate/comparator.
4. `LogicManager` skips `Storage#saveAddressBook` for `FindCommand`, `FilterCommand`, `SortCommand`, `ListCommand`, `SquadCommand`, `HelpCommand` and `ExitCommand`. Other successful command types save `model.getAddressBook()`, even if their feedback reports no data change.
5. A `CommandResult` contains feedback and flags for showing help or exiting. On an `IOException` while saving, `LogicManager` logs the technical cause and returns the applied command feedback followed by `MESSAGE_SAVE_FAILURE`. A failed `SaveCommand` instead returns `SaveCommand.MESSAGE_FAILURE`. Neither failure rolls back the model or throws a save-related `CommandException`, so the UI clears the command box and does not invite a repeated mutation.

This partial sequence shows parsing and model mutation for `delete 1`; the later storage write is shown in the architecture sequence above:

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions inside Logic for delete 1" />

<box type="info" seamless>

The `DeleteCommandParser` lifeline should end at its destroy marker (X); PlantUML continues the lifeline visually.
</box>

The parser helper diagram is partial: `XYZCommandParser` denotes a family of real
classes, including `RateCommandParser`, `SortCommandParser`, `SetStatusCommandParser`,
`RemarkCommandParser`, `InjuryCommandParser` and `TagCommandParser`. `keep`, `cut` and `pending` share
`SetStatusCommand` and its status-specific parser instance. Tagged arguments use
`ArgumentTokenizer`, `ArgumentMultimap`, `CliSyntax` and `Prefix`; positional rating
and sort arguments use whitespace splitting instead.

<puml src="diagrams/ParserClasses.puml" width="650" alt="Command parser family and shared argument helpers" />

### Model component
**API:** [`Model.java`](https://github.com/AY2627S1-CS2103T-W13-1/tp/blob/master/src/main/java/seedu/address/model/Model.java)

The model is shown in four class views: contracts, roster data, identity, and player assessments. Repeated class
names refer to the same classes across views. Interface realization uses a dashed
line with a hollow triangle pointing to the interface. Association arrowheads show
navigability; multiplicities describe the number of objects at each end.
Notation follows the [course UML reference sheet](https://nus-cs2103-ay2627-s1.github.io/website/admin/uml-reference-sheet.html).

<iframe src="diagrams/ModelClassDiagram.html" title="CouchCoach model UML class diagrams"
        width="100%" height="2400" style="border: 0;"></iframe>

[Open the editable HTML diagram](diagrams/ModelClassDiagram.html).
The corresponding PlantUML sources are
[model contracts](diagrams/ModelClassDiagram.puml),
[roster data](diagrams/ModelPlayerClassDiagram.puml),
[player identity](diagrams/ModelIdentityClassDiagram.puml), and
[player assessments](diagrams/ModelAssessmentClassDiagram.puml).


The `Model` component,

* stores the team's player data as `Person` objects, which are contained in a `UniquePersonList` object.
* derives a `FilteredList<Person>` from the roster, a `SortedList<Person>` from that filtered list, and an unmodifiable observable wrapper for display. `getFilteredPersonList()` exposes that final displayed view; these wrappers reference the same immutable players rather than making player copies.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

Each `Person` (a player) holds one `Name`, one `Phone`, one `SelectionStatus`, one
`Rating`, one `Remark`, one `InjuryStatus`, zero or one `JerseyNumber`, and zero or more
`Tag` values. Empty remarks
and unrated ratings are present value objects, so their multiplicity is still `1`.
These are ordinary associations: immutable
values can be shared between player versions, so deleting one player does not imply
deleting its value objects. `AddressBook` exclusively contains its `UniquePersonList`,
which is shown using composition. The full roster and displayed view reference the
same player objects; filtering and sorting do not create copies.

The view pipeline is `AddressBook#getPersonList()` → `filteredPersons` →
`sortedPersons` → `displayedPersons`. `updateFilteredPersonList(predicate)` changes
which roster entries are visible; `setPersonComparator(comparator)` changes their
display order. Neither operation changes the source roster. `PersonListPanel` binds
to the observable display and numbers its cells from 1. Index-based commands use
that same view, so they must select the target before replacing it: replacement may
change filter membership or sorted position. `ListCommand` clears the comparator
and restores `PREDICATE_SHOW_ALL_PERSONS`.

`UniquePersonList` uses `Person#isSamePerson` to reject duplicates: two players are the
same if their names match after ignoring case and repeated spaces. Phone numbers,
jersey numbers and tags do not affect identity: players such as siblings may share a
parent's contact number, and `add` and `edit` accept a shared jersey number, only
warning the coach about it. Routine accessors, library collection wrappers, and unrelated methods
are omitted from the diagrams. The static tag-validation operation is underlined.

Edit the PlantUML sources, then run `python3 docs/scripts/generate_model_diagrams.py`
from the repository root with Java 25 selected (install documentation dependencies
with `npm ci --prefix docs` first). The script uses MarkBind's bundled PlantUML renderer
to regenerate the self-contained HTML and its inline SVG; do not edit generated SVG
separately. This keeps one diagramming source for each displayed figure. The HTML
also includes the editable source text. The shared legend lists only relationships
used across the four views.

The identity view includes the optional jersey number; the assessment view includes
selection, rating, remarks and injury status. The [rating and sorting UML](diagrams/player-rating-sorting-uml.html)
retains the separate command-level view. Feature authors maintain their detailed
implementation diagrams; these shared views describe component/model relationships.


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-W13-1/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="600" alt="Class diagram of the Storage component" />

The `Storage` component,
* saves the team and the user preferences as JSON files, and reads them back into model objects.
  `StorageManager` implements `Storage` and delegates to one class per file: `JsonAddressBookStorage` for the team
  (`data/addressbook.json`) and `JsonUserPrefsStorage` for window settings (`preferences.json`). Both paths are relative
  to the working directory, i.e. the folder the app is launched from.
* depends on some classes in the `Model` component, because its job is to save and retrieve objects that belong to the `Model`.

**JSON adaptation.** `JsonSerializableAddressBook` holds a list of `JsonAdaptedPerson`, which stores every player field
as a `String` (or a list of `String` for tags), so that Jackson can map it to JSON directly. Converting back with
`JsonAdaptedPerson#toModelType()` validates each field with the same rules the commands use, e.g. `Phone#isValidPhone`
and `Tag#isValidTagName`. `JsonSerializableAddressBook#toModelType()` additionally rejects a `null` entry in the
`persons` list and two players with the same name (as defined by `Person#isSamePerson`).

**Defaults and backward compatibility.** A missing optional field takes the default a new player would have; name and phone remain required:
`PENDING` selection status, unrated, fit, no tags, an empty remark and no jersey number. Data files saved by earlier
versions, which lack the newer fields, therefore still load. Conversely, `JsonAdaptedPerson` leaves out an unset
jersey number when saving, and writes a `null` rating for an unrated player. Jackson also accepts plain integer JSON values for rating and jersey number by converting them to strings before model validation; the next save writes their canonical digit strings.

**Loading at startup.** `MainApp#initModelManager` reads the team once, when the app starts:

<puml src="diagrams/StorageLoadActivityDiagram.puml" width="550" alt="Activity diagram of how the team is loaded at startup" />

`JsonUtil#readJsonFile` returns an empty `Optional` for a missing file, and throws a `DataLoadingException` for a file that
cannot be parsed. `JsonAddressBookStorage` also turns an `IllegalValueException` from the conversion into a
`DataLoadingException`, so an invalid file reaches the same fallback whatever is wrong with it: an empty team plus
`Messages.MESSAGE_DATA_FILE_UNREADABLE`, which `UiManager` shows in the result box. The team file is not written at startup, so the
invalid file stays on disk for the coach to fix. An unparsable `preferences.json` similarly falls back to default window settings.

`JsonUtil#readJsonFile` also rejects a document containing only `null` with a `DataLoadingException`.
`JsonUserPrefsStorage` rejects null GUI settings before preferences reach the model. Invalid preferences
fall back to defaults and are rewritten at startup; the team file is unaffected.

**Saving after a command.** The `Model` holds the team in memory; the file is only written by `LogicManager#execute`:

<puml src="diagrams/StorageSaveActivityDiagram.puml" width="650" alt="Activity diagram of how LogicManager decides whether to save" />

* `LogicManager#isReadOnly` lists the command types that never change data (`list`, `find`, `filter`, `sort`, `squad`,
  `help` and `exit`). Every other command type saves after it executes successfully, even when it changed nothing
  (e.g. `keep` on a player who is already kept). This can save changes from earlier failed attempts.
* A command that is rejected throws a `ParseException` or `CommandException` before the save step, so the file is unchanged.
* Each save writes the whole team through `JsonAddressBookStorage#saveAddressBook`, which also recreates a missing `data`
  folder via `FileUtil#createIfMissing`.
* If the save throws an `IOException` (e.g. the file or folder is read-only), `LogicManager` returns the applied
  command feedback plus `MESSAGE_SAVE_FAILURE`. A failed explicit `save` returns `SaveCommand.MESSAGE_FAILURE` instead.
  The change stays in the in-memory `Model`, but the save is unsuccessful; the UI clears the command box.
  `FileUtil#writeToFile` writes directly to the destination with `Files.write`, so an
  I/O failure can leave the file unchanged, empty or partially written. A later successful save writes the whole
  in-memory team, including all changes whose earlier saves failed. `SaveCommand#execute` only returns feedback;
  `LogicManager` performs the actual write because `SaveCommand` is not classified as read-only. It writes
  the complete roster even while a filter or sort is active. Closing the app never retries a team save.

**Design considerations.**

**Aspect: When to save**

* **Alternative 1 (current choice):** Save the whole team after every command that may change data.
  * Pros: Simple; each successful save writes the current whole team, including changes from any number of
    earlier failed saves. Consecutive failures can leave multiple commands unsaved until a save succeeds.
  * Cons: Rewrites the whole file for every change, which is fine for a team-sized roster but would not scale to very large files.
* **Alternative 2:** Save only when the app exits.
  * Cons: A crash or a failed final save loses every change of the session.

**Aspect: What to do with an invalid data file**

* **Alternative 1 (current choice):** Start with an empty team, warn, and leave the file untouched until the next data-changing command or explicit `save`.
  * Pros: The coach can close the app and repair or restore the file without losing it.
  * Cons: A careless change after the warning overwrites the invalid file.
* **Alternative 2:** Refuse to start.
  * Cons: The coach cannot use the app at all until the file is fixed outside it.

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Shared command parsing and prefix boundaries

`AddressBookParser` separates a lowercase command word from its arguments. It accepts ordinary whitespace and non-breaking spaces around that word. `ArgumentTokenizer` recognises only the prefixes supplied by the command parser, each preceded by an ordinary space, tab or non-breaking space. It scans separator positions, so an empty prefix cannot cause an infinite loop. Joined text such as `Amy p/123` has a valid phone delimiter; `Amyp/123` does not. Line breaks do not delimit prefixes.

`CommandBox` converts pasted tabs to spaces for JavaFX input, but the parser and tokenizer are also called directly by logic clients and tests. Keep tab recognition in `ArgumentTokenizer` so raw command strings have the same prefix boundaries without depending on a UI preprocessing step. The command-box formatter protects the paste path; tokenizer support preserves the parser contract for other callers. Non-breaking spaces are not converted by the formatter and still need tokenizer support.

The tokenizer trims ordinary control/space padding and non-breaking spaces from each value and the preamble; it preserves internal text for the field validator. `ArgumentMultimap` retains repeated values. Parsers reject repeated single-value prefixes, while `t/` deliberately repeats. Unknown prefixes remain part of the preceding value unless a parser explicitly recognises them to provide targeted guidance, as `add` and `edit` do for `r/` and `i/`.

`ParserUtil#parseIndex(String, String)` accepts a positive displayed index, including `+1` and `01`, and converts it to an `Index`. Zero, negative and overflowing whole numbers receive the invalid-player-index message; malformed text receives command usage. `find`, `rate` and `sort` split positional arguments with Java's `\s+` pattern, which does not treat non-breaking spaces as argument separators. Their examples therefore use ordinary spaces.

### Jersey number normalization

`JerseyNumber` validates a numeric value from 0 to 99 with optional leading zeros.
The immutable numeric value supplies equality, display, and JSON serialization, so `099`
and `99` produce the same stored number and duplicate warning. Validation rejects
out-of-range digits before integer parsing, including padded values such as `0100`.

### Adding players, jersey numbers and duplicate warnings

#### Implementation

`add` creates a new `Person` from a name, a phone number, an optional jersey number and optional tags. The sequence diagram below shows how `add n/Amy Tan p/91234567 j/7` is executed.

<puml src="diagrams/AddSequenceDiagram.puml" alt="Interactions inside the Logic, Model and Storage components for an add command with a jersey number" />

<box type="info" seamless>

**Note:** The lifeline for `AddCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

1. `AddCommandParser` tokenizes the arguments. It also looks for `r/` and `i/`, but only so that `ParserUtil#requireNoRemarkOrInjury()` can reject them with a message that names the right command. Without this, `r/...` would be read as part of the value before it, and the error would blame that value instead. `n/` and `p/` are compulsory, and `ArgumentMultimap#verifyNoDuplicatePrefixesFor()` rejects a repeated `n/`, `p/` or `j/`.
1. `ParserUtil` parses each value. `parseName()` collapses repeated spaces with `Name#normalize()` before checking the allowed characters and the 100-character limit separately, so that each problem has its own message. `parsePhone()` removes spaces, hyphens, dots and brackets with `Phone#normalize()` before checking for 3 to 15 digits. `parseJerseyNumber()` accepts values from 0 to 99 with optional leading zeros, and `JerseyNumber` stores the value as an `int`, so `07` and `7` are the same number.
1. The parser creates the player with `new Person(name, phone, tags)`, which starts as `PENDING`, `UNRATED`, `FIT` and with an empty remark, and sets the jersey number with `Person#withJerseyNumber()`. A `Person` stores a missing jersey number as `null` but exposes it only as an `Optional<JerseyNumber>` from `getJerseyNumber()`.
1. `AddCommand#execute()` rejects the player if `Model#hasPerson()` finds a player with the same name. This uses `Person#isSamePerson()`, which compares names with `Name#isSameName()`, ignoring case and repeated spaces. Otherwise, `Model#addPerson()` adds the player and resets the filtered list to show the whole team, so the coach sees the new player.
1. `AddCommand` then asks two package-private helpers in the `logic.commands` package for warnings. `DuplicateJerseyNumberChecker#getWarning()` looks for other players with the same jersey number (`Person#hasSameJerseyNumber()`), and `SimilarNameChecker#getWarning()` looks for names that are equal once spaces and punctuation are ignored (`Name#isSimilarTo()`). Both search `Model#getAddressBook()`, i.e. the whole team rather than the filtered list, so they also find players hidden by `find` or `filter`. Each returns an empty string or a warning line, which is appended to the success message.
1. `LogicManager` saves the team through `Storage`. `JsonAdaptedPerson` writes `jerseyNumber` only for a player who has one, and reads a missing field as no jersey number.

`edit` reuses the same parsing and checks, with these differences:

* `EditPersonDescriptor` records whether `j/` was given in `isJerseyNumberEdited`, as a `null` jersey number can mean either "not given" or "remove it". `EditCommandParser#parseJerseyNumberForEdit()` turns an empty `j/` into `null`, which removes the jersey number.
* The duplicate-name check runs only when the new name belongs to another player (`!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)`), so a coach can change the capitalisation or spacing of a player's own name.
* `EditCommand` builds the edited player from the old one, so the selection status, rating, remark and injury status are always kept, and so are the tags. The jersey number is kept unless `j/` is given. `TagCommand` reuses the edit descriptor internally with the union of existing and requested tags, or an empty set to clear them. If the result equals the old player, the command reports that nothing changed instead of replacing it.
* The jersey number warning is checked only when the edit includes `j/`, and the similar-name warning only when the name actually changes. This stops edits to other details from repeating a warning the coach has already seen.
* `Model#setPerson()` replaces the player without resetting the filtered list, so the current search and sort remain active. A rename that stops matching the search removes that player from the displayed view and renumbers later results.

#### Design considerations

**Aspect: How to handle a jersey number that is already in use**

* **Alternative 1 (current choice):** Accept it and warn the coach, naming the other players with that number.
  * Pros: Numbers can legitimately repeat, e.g. training bibs at trials, or players from different age groups in one team. Swapping two players' numbers needs one moment where both share a number, which a hard rule would block.
  * Cons: A coach who ignores the warning can end up with two players on the same number.
* **Alternative 2:** Reject the command, like a duplicate name.
  * Pros: Every jersey number identifies one player.
  * Cons: Blocks the legitimate cases above, and makes a swap take three commands.

**Aspect: What makes two players the same player**

* **Alternative 1 (current choice):** The same name, ignoring case and repeated spaces. Names that differ only in punctuation or spacing within words, such as `TanWei Ming` and `Tan Wei Ming`, are allowed but trigger a warning.
  * Pros: Catches the common mistake of adding the same player twice, while still allowing two players who share a phone number or jersey number. A warning, rather than a rejection, allows names that are spelt differently on purpose.
  * Cons: Two different players with exactly the same name need something added to one name, e.g. `Jr`.
* **Alternative 2:** The same name and phone number.
  * Pros: Two players with the same name can be added without changing either name.
  * Cons: The same player added twice with different contact numbers is not caught.

**Aspect: Where the warning checks live**

* **Alternative 1 (current choice):** Separate helper classes in `logic.commands`, called by `AddCommand` and `EditCommand` after the change is made.
  * Pros: The warning text is command feedback, so the `Model` stays unaware of it. Both commands share one implementation.
  * Cons: Each check scans the whole team once per command. The non-functional requirements target responsive commands with 1000 players; this is a performance target, not an enforced roster limit.
* **Alternative 2:** Check inside `UniquePersonList` when a player is added or replaced.
  * Cons: The model would have to return warnings alongside its changes, mixing presentation into the model.

### Player ratings (US12)

`RateCommandParser` requires two positional arguments for `rate INDEX RATING`.
`ParserUtil#parseIndex` validates the positive one-based index; `RateCommand#execute`
checks that it exists in the displayed view. `Rating#isValidRating` accepts only
`0`–`9` or `10`, so decimals, signs and padded scores such as `08` are rejected.
The case-insensitive keyword `unrated` maps to `Rating.UNRATED`.

<puml src="diagrams/RateSequenceDiagram.puml" alt="Parse rate 1 8, replace the selected player and save the roster" />

The command selects the immutable `Person` from `Model#getFilteredPersonList()`
and replaces it through `Model#setPerson`, preserving name, phone, selection status,
tags, remark, injury status and optional jersey number. The current predicate and
comparator remain active, so a rating update may immediately change displayed indices.
Removing an already absent rating returns the already-unrated feedback without a
replacement. `LogicManager` nevertheless saves after every successful `RateCommand`,
because saving is classified by command type. Invalid requests return before saving.
If the write fails, the changed model remains visible and the save-failure feedback
explains that it has not reached disk.

**Design decisions.** `Rating` is a value object with an explicit `UNRATED` value,
rather than using zero as a sentinel or nullable ratings throughout commands. This
keeps a legitimate score of 0 distinct from not assessed and gives every `Person`
a rating value. Immutable player replacement preserves unrelated fields and sends
observable list events through the existing UI pipeline; it requires copying those
fields correctly, which is covered by `RateCommandTest` and persistence tests.

`JsonAdaptedPerson` writes numeric ratings as strings and an unrated rating as JSON
null. A missing or null **rating field** loads as `UNRATED`; this is distinct from
an invalid top-level team document. The saved roster retains insertion order.

The editable sequence source is [RateSequenceDiagram.puml](diagrams/RateSequenceDiagram.puml).
Its [HTML view](diagrams/RateSequenceDiagram.html) is generated with
`python3 docs/scripts/generate_rate_sequence.py`; use `--check` to detect drift.

<span id="rating-sort-scopes"></span>

### Rating sorting (US17) and list reset (US02)

`SortCommandParser` accepts `rating` with an optional `all`/`squad` scope and
`asc`/`desc` direction, in either order and any letter case. No direction means
`DESCENDING`; no scope means `CURRENT`. Duplicate, unknown and conflicting options
are rejected before execution. `SortCommand` stores `Direction` and `Scope` enum
values. Its `updateScope(model)` retains the predicate for `CURRENT`, replaces it
with `PREDICATE_SHOW_ALL_PERSONS` for `ALL`, or uses the shared
`HasSelectionStatusPredicate.IS_KEPT` for `SQUAD`, then applies its rating comparator.
Explicit scopes replace rather than intersect the previous name/tag/injury filter.
`Direction.ASCENDING` and `Direction.DESCENDING` determine the numeric ordering.

A scope is a one-time view selection rather than a persistent restriction. `find`,
`filter` and `squad` replace the predicate but retain the comparator. `add` restores
the all-player predicate while retaining the comparator; `edit`, `tag` and `rate`
retain both. Tag/name updates may remove a player from a filtered view; a rating
update may move the player within the sorted view.

**Comparator design.** Rated players precede unrated players in both directions.
Only the comparison of numeric scores is reversed for descending order; reversing
the whole comparator would incorrectly put unrated players first. `ModelManager`
appends the player's index in `AddressBook#getPersonList()` as a tie breaker. Equal
scores and multiple unrated players therefore keep source insertion order even
after replacement events. Source-index lookup is linear per tie comparison, a
simple tradeoff for team-sized rosters rather than maintaining another identity index.
Relying on the previous visual order or sort stability alone would not express the
required insertion-order tie rule after rating changes.

**Displayed indices and storage.** Commands receive the unmodifiable sorted view;
index 1 identifies its first player, not the first stored player. The comparator is
applied by JavaFX `SortedList` rather than sorting `UniquePersonList` in place.
`LogicManager` skips saving for `SortCommand` and `ListCommand`, and later mutations
save `getAddressBook()` rather than the view. Sorting can therefore be changed without
rewriting player data or losing insertion order. On restart `ModelManager` creates
new unfiltered, unsorted views; scores persist, predicates/comparators do not.

`ListCommand` calls `setPersonComparator(null)` and then
`updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS)`. It succeeds on an empty roster,
does not restore deletions, and reports `Listed all players`. `AddressBookParser`
ignores its arguments, so `list all` remains equivalent to `list`.

### Selection status feature

#### Implementation

Each `Person` has a `SelectionStatus`, an enum with the values `PENDING`, `KEPT` and `CUT`. New players start as `PENDING`. The `keep`, `cut` and `pending` commands change it, and all three are handled by one `SetStatusCommand` class, which takes the status to set. `AddressBookParser` picks the status from the command word and passes it to a `SetStatusCommandParser`, which parses the index.

The sequence diagram below shows how `cut 1` is executed.

<puml src="diagrams/CutSequenceDiagram.puml" alt="Interactions inside the Logic, Model and Storage components for the `cut 1` command" />

<box type="info" seamless>

**Note:** The lifeline for `SetStatusCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

1. `AddressBookParser` matches the command word `cut`, creates a `SetStatusCommandParser` for `CUT`, and uses it to parse `"1"` into a `SetStatusCommand`.
1. `LogicManager` executes the command. The command gets the player `p` at index 1 of the displayed list from the `Model`.
1. As `Person` is immutable, `p.withSelectionStatus(CUT)` returns a new `Person` with every other field (name, phone, rating, tags, remark, injury status and jersey number) unchanged. The command replaces `p` with it using `Model#setPerson()`.
1. `LogicManager` then saves the team through `Storage`. It decides this by command type: read-only command types, such as `find` and `squad`, skip this step, while `keep`, `cut` and `pending` always save.

If the index is out of range, the command throws a `CommandException` before changing anything. If the player already has the status, the command returns a message saying so and does not replace the player. `LogicManager` still saves in this case, as it only checks the command type, so this also saves any earlier unsaved changes.

The status is saved in the data file as a `selectionStatus` field. Data files saved by v1.3 have no such field, so `JsonAdaptedPerson` loads those players as `PENDING`.

**Viewing the squad.** `squad` is handled by `SquadCommand`, which does not build a separate list. It sets the `Model`'s
displayed-list predicate to `HasSelectionStatusPredicate(KEPT)`, so the squad is simply the kept players in the displayed list.
As a result:
* any active rating sort still applies, because `ModelManager` keeps its comparator when the predicate changes;
* `keep`, `cut`, `pending` and `delete` use the squad's numbering while it is shown, and a player who is cut or marked
  pending immediately leaves the displayed list;
* `SquadCommand` changes no data, so `LogicManager` lists it as read-only and does not save after it.

#### Design considerations

**Aspect: How to represent the three commands**

* **Alternative 1 (current choice):** One `SetStatusCommand` that takes the status to set.
  * Pros: No repeated code. Adding a new status needs a new enum value and command word, a case in `SetStatusCommand#getCommandWord()`, and a case in `AddressBookParser`.
  * Cons: The shared class needs a mapping between each status and its command word, which the separate classes would not need.
* **Alternative 2:** Separate `KeepCommand`, `CutCommand` and `PendingCommand` classes.
  * Pros: Each class matches one command word, like the rest of AB3.
  * Cons: Three almost identical classes and parsers to keep in sync.

**Aspect: How to show the squad**

* **Alternative 1 (current choice):** A predicate on the displayed list (`HasSelectionStatusPredicate`).
  * Pros: Reuses the filtered and sorted list that every other view uses, so indices, sorting and the UI need no special handling.
  * Cons: The squad replaces any active `find` or `filter`, as the displayed list has one predicate at a time.
* **Alternative 2:** A separate squad list kept in the `Model`.
  * Cons: Two lists to keep in sync whenever a status, a player or the sort order changes.

**Aspect: What the list shows after the command**

* **Alternative 1 (current choice):** Keep the current filter.
  * Pros: A coach can `find Tan` and then mark each result in turn without searching again.
  * Cons: When the filter depends on the status, as in `squad`, the change can move the player out of the filter. For example, `cut 1` while viewing the squad removes that player from the displayed list, and the players below move up one index.
* **Alternative 2:** Reset to the full list, like `add`.
  * Cons: The coach loses their place after every decision.

### Custom player tags (US08)

`Person` holds an immutable set of `Tag` values. The model class diagram above shows
its zero-to-many association with tags. Tags are shared values rather than part of
player identity: duplicate detection remains based on the normalized player name.
`Tag` trims surrounding whitespace, collapses internal whitespace, validates the allowed characters and the 30-code-point length limit, and preserves display spelling. Equality and hashing use the same case-folded label, so case variants cannot become separate tags. Tags do not participate in player identity.

`AddCommandParser` collects initial `t/` values through `ParserUtil.parseTags`. Its `LinkedHashSet` keeps the first spelling among case-insensitive duplicates. `TagCommandParser` parses `tag INDEX t/TAG [t/TAG]...`: supplied labels are appended to the existing set, and a single empty prefix clears it. Missing prefixes and empty labels mixed with other labels fail before any field changes. `EditCommandParser` rejects tag prefixes with migration guidance before changing any fields; its public syntax accepts only name, phone and jersey edits. There is no single-label removal command: clear the tags, then add the labels to keep.

`TagCommand` defensively copies the requested tags. At execution, it merges them into a copy of the displayed player's existing tags, inserting existing values first so case-insensitive duplicates retain their saved spelling. An empty request instead produces an empty set. It delegates player replacement to an `EditCommand` containing only this merged tag descriptor, sharing field preservation and filter handling. `TagCommand.execute` and `EditCommand.execute` resolve the index against `Model#getFilteredPersonList`, which exposes the displayed, possibly sorted view. It creates a replacement `Person`, preserving fields omitted from the descriptor, including rating, selection status, remark, injury status and jersey number. The descriptor and player defensively copy tag sets so later caller changes cannot mutate a stored player. `Model#setPerson` replaces the roster entry without resetting the predicate or comparator. Appending tags retains existing tag-filter membership. Clearing tags removes the player from a tag-filtered view; the remaining displayed indices change accordingly.

Supplying only existing labels, including case variants or a subset, preserves their spelling and returns the shared `No changes made` result. `TagCommand` formats successful additions with the complete final tag set as `Updated tags for NAME: LABELS`. Clearing all tags reports `NAME: tags cleared.` An already empty set instead reports no changes. `LogicManager` saves after a successfully executed tag command, including a no-change result; parse and execution failures do not save. A save failure leaves the in-memory change applied and appends the save warning to the tag command feedback. The coach retries with `save` after fixing storage; the tag update is not repeated.

`JsonAdaptedPerson` writes tag strings in case-insensitive alphabetical order and accepts missing/null `tags` from older records as an empty collection; invalid/null entries are rejected. `PersonCard` displays tags alphabetically in a wrapping `FlowPane`. Keeping tags as immutable values shared by parser, model and storage gives updates and filtering the same label rules without coupling identity to a coach's changing categories.

### Filtering players by tag (US16)

`FilterCommandParser` accepts repeated `t/` arguments and reuses `ParserUtil.parseTags`. It also accepts one optional `i/` injury status and requires at least one condition. `TagsContainAnyPredicate` holds a defensive copy of the requested tags and matches any complete label using `Tag` equality (OR). `FilterCommand#matches` combines that result with the optional injury status using AND. An injury-only filter needs no tag predicate. Invalid labels, an invalid/repeated injury status or unexpected preamble fail before the view changes.

`FilterCommand` replaces the model's list predicate, so each filter searches the full roster rather than intersecting with an earlier name search, filter or squad view. `LogicManager` treats filtering as read-only and skips saving, including when the data file is unreadable or unwritable.

<puml src="diagrams/TagFilterClassDiagram.puml" width="650" />

This partial UML shows the tag-matching path; the optional injury-status condition is omitted. The parser creates the command and optional tag predicate. The predicate realizes `Predicate<Person>` and reads player tags; the command changes the filtered view through `Model`.

`ModelManager` wraps the roster in a `FilteredList`, then a `SortedList`, and exposes an unmodifiable observable view. Changing the predicate retains the active rating comparator. Results therefore use rating order when sorting is active, otherwise insertion order, and are numbered from 1. `find`, `filter` and `squad` replace one another's predicate while retaining sorting; `list` clears both predicate and comparator. This separates a coach's view from persisted roster order and avoids changing records just to browse a group.

### Player remarks and injury status (US21 / US13)

`RemarkCommandParser` requires a displayed index and exactly one `r/`. An empty value constructs an empty `Remark` to clear the note; repeated `r/` delimiters reject the whole command. `Remark` is an immutable free-text value without a length limit. Other prefix-like text is literal because this parser recognises only `r/`. `RemarkCommand` selects the displayed player and replaces it using `Person#withRemark`, then reports the text or `(cleared)`. If the new remark equals the current one, it returns the shared `No changes made` result instead and does not replace the player.

`InjuryCommandParser` requires an index and exactly one `i/`. `ParserUtil#parseInjuryStatus` accepts `fit` or `injured` in any letter case. `InjuryCommand` uses `Person#withInjuryStatus`; if the value is already set it returns already-set feedback without replacement. Injury is independent of selection and rating: an injured player can still be kept and appear in `squad`.

Both commands preserve every unrelated field and retain the predicate and comparator. Changing injury can remove a player from an injury-filtered view immediately, so subsequent indices can change. `LogicManager` saves after either successful command, including a repeated remark or already-set injury. Invalid syntax and out-of-range indices return before persistence. Save failures use the shared retry flow above.

`JsonAdaptedPerson` writes remark text and the lowercase injury status, defaulting missing/null fields to an empty remark and `FIT` when loading older files. `PersonCard` displays `Remark: TEXT` only for a non-empty remark, and an **Injured** badge only for `INJURED`. This keeps assessment notes alongside the player while avoiding a redundant Fit badge on every card.

### Name search implementation (F3 / US04)

`AddressBookParser` dispatches `find` to `FindCommandParser`, which rejects an empty argument string and splits plain keywords on whitespace. `NameContainsKeywordsPredicate` accepts a player if any keyword matches a whole whitespace-delimited word of the original name or a complete part separated by punctuation. It uses `StringUtil.containsWordIgnoreCase` for both representations. Thus both `Wei-Ming` and `Ming` match `Tan Wei-Ming`, while `Min` does not. Letters, combining marks and numbers stay together when punctuation is replaced by spaces; slashes in keywords remain literal, so `s/o` can match the original name.

A third representation removes punctuation from the start and end of each word, e.g. `Shaun ((O'Neil))` becomes `Shaun O'Neil` and `Tan Wei-Ming, Jr.` becomes `Tan Wei-Ming Jr`. The keyword is trimmed the same way and compared with this representation and with the parts, so `O'Neil`, `(O'Neil)` and `((O'Neil))` all match `Shaun ((O'Neil))`, and `(lee)` matches `David(lee)`. A keyword that is only punctuation, such as `()`, trims to nothing and matches no player. These checks are added to the original two with a logical OR, so they never remove a match. The predicate never joins parts or looks inside a word: `lee` and `l(e(e))` do not match `David(l(e(e)))`, whose parts are `David`, `l`, `e` and `e`, consistent with `Min` not matching `Wei-Ming`.

`FindCommand` calls `Model#updateFilteredPersonList`, using the same filtered/sorted view described above. Each new search covers the whole roster, ignores phone/tag/remark fields, shows each match once, and returns singular or plural match-count feedback. Zero matches is a successful empty view. `LogicManager` skips saving; invalid syntax leaves the previous view unchanged. Predicate-based matching keeps search independent of JavaFX cards and storage while allowing indexed commands to act on exactly the displayed results.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is a coach of a sports team, at any level (e.g. school, club or community team)
* has a significant number of players to keep track of, especially during trials or team selection
* needs to record details and observations about each player
* needs to rank players and decide who to keep or cut from the team
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**:

CouchCoach brings player contact details, jersey numbers, categories, observations, ratings and selection decisions into one local roster. Coaches can find a player quickly, review a tagged group and compare players before finalising their squad.

**Boundaries**:

* The product supports recording players and observations, organising the trial pool, comparing ratings and recording selection decisions, from intake through the final squad list.
* It is a single-user desktop application for coaches of any sports team. Match-day tactics, training-plan design, contacting players and shared concurrent editing are outside the product scope.
* Player records are saved locally. The current release does not implement undo/redo, data archiving, partial-string name search, emergency contacts, pagination, statistics or squad export. Historical brainstorms in the team notes are not promises of release functionality.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

The IDs and priorities below retain the [team notes](https://docs.google.com/document/d/1dSIQTzhhsTZZvvx7wB0HhxJKVrBdz_BInOlgk_0DiIQ/edit) identifiers used by feature sections and use cases. The notes also contain historical MVP specifications and unapproved proposals; the release coverage column describes the implemented subset. F1–F7 refer to the original MVP feature IDs, not a new numbering scheme for later features.

| ID | Priority | As a … | I want to … | So that I can … | Release coverage |
| --- | --- | --- | --- | --- | --- |
| US01 | `* * *` | coach | record a player's name and contact number | keep a record of everyone in the team | `add` (F1) |
| US02 | `* * *` | coach | list all players | see everyone in the team at a glance | `list` (F2); full roster in insertion order, clearing filters and sorting |
| US03 | `* * *` | coach | delete a player | remove withdrawn or mistaken entries | `delete` (F4) |
| US04 | `* * *` | coach | find players by name | retrieve details without scanning the entire roster | Whole-word and punctuation-part `find` (F3) |
| US05 | `* * *` | coach | view command formats inside the app | recall syntax without leaving the app | `help` (F5) |
| US06 | `* * *` | coach | have player changes saved automatically | resume work without re-entering changes | Automatic local saving (F7), with failure feedback and an explicit `save` retry |
| US07 | `* * *` | coach | edit a player's details | correct mistakes without re-entering the record | `edit` |
| US08 | `* * *` | coach | assign my own labels to players | organise positions, roles, seasons and trial groups | Tags through `add` and `tag` |
| US09 | `* * *` | coach | record a player's jersey number | identify them from trial notes and videos | Optional jersey number through `add` and `edit` |
| US10 | `* * *` | coach | mark players as kept, cut or pending | track and revise selection decisions | `keep`, `cut`, `pending` |
| US11 | `* *` | coach | be warned about duplicate names | avoid duplicate player records | Exact duplicate rejection and similar-name warnings |
| US12 | `* *` | coach | give players numeric ratings | compare assessed players on a common scale | `rate` from 0 to 10, including removal with `unrated`; Unrated is distinct from 0 |
| US13 | `* *` | coach | mark a player as injured | distinguish injury from a selection decision | `injury` and injury filtering |
| US14 | `* *` | coach | see which players remain unrated | notice players who still need evaluation | Unrated cards and placement after rated players when sorting; no dedicated unrated filter |
| US16 | `* *` | coach | filter players by tag | focus on one group | `filter`, with OR across tags and optional injury condition |
| US17 | `* *` | coach | sort players by an attribute | compare players in a useful order | Rating only, on the current view, full roster or kept squad, in either direction; other attribute sorts are not implemented |
| US18 | `* *` | coach | view the players I have kept | see the resulting squad | `squad` |
| US21 | `*` | coach | add a free-text remark | retain qualitative observations alongside ratings | One replaceable remark per player |
| US27 | `*` | coach | clear all player records | start a fresh roster | `clear` |

The historical backlog also names US15 (partial-string search), US19 (emergency contacts), US20 (undo), US22 (remark timestamps), US23 (arbitrary combined criteria), US24 (pagination), US25 (statistics) and US26 (top-N shortlisting). These are unimplemented ideas, not committed release requirements. The supported tag-plus-injury combination does not implement arbitrary combinations of name search, squad and other filters. F6 supplies `exit` without a separate story ID in the notes.

### Use cases

For all use cases below, the **System** is **CouchCoach** and the
**Actor** is the **Coach**, unless specified otherwise.

These use cases describe player-management workflows. The original MVP covered deletion; editing, categorisation, ratings, selection decisions and remarks are now implemented too. Consult each feature's implementation and manual checks for supported syntax and limits.

The following rules apply throughout:

* A player selected from a displayed list refers to a player in that list,
  which may contain all players or only search results. For deletion,
  selection uses the player's current index in the displayed list.
* Duplicate-name detection is part of the MVP. Names are compared after
  trimming leading and trailing spaces, collapsing repeated internal spaces,
  and ignoring letter case. Players may share a contact number.
* Editing preserves the same name-uniqueness rule:
  changing a player's name must not duplicate another player's name.
* Rejected requests leave player data unchanged and do not trigger a save.
* Successful data changes are saved automatically. If saving
  fails, the change remains applied in the current session, and CouchCoach
  warns that the change has not been saved and may be lost after closing.

#### UC01: Delete a player

**Related user stories:** US02, US03, US04, US06

**MSS**

1. Coach requests to list all players.
2. CouchCoach displays the player list.
3. Coach requests to delete a specific player, using the player's index in the displayed list.
4. CouchCoach deletes the player, saves the updated team, and confirms which player was deleted.
   The displayed list keeps its current search, filter or sort order, and the remaining players are renumbered.

   Use case ends.

**Extensions**

* 1a. Coach instead requests a narrowed or reordered list: players found by name, players with given tags or
  injury status, the kept squad, or players sorted by rating.

    * 1a1. CouchCoach displays that list.

      Use case resumes at step 2. The same view remains after the deletion.

* 2a. The displayed list is empty.

  Use case ends.

* 3a. The request does not contain exactly one index that is a whole number.

    * 3a1. CouchCoach shows the correct command format and leaves the team unchanged.

      Use case resumes at step 3.

* 3b. The index is a whole number that does not identify a player in the displayed list (e.g. 0, negative, or too large).

    * 3b1. CouchCoach shows that the player index is invalid and leaves the team unchanged.

      Use case resumes at step 3.

* 4a. CouchCoach cannot save the updated team.

    * 4a1. CouchCoach informs the Coach that the deletion was applied but could not be saved, and will be lost
      if the app is closed before the Coach fixes storage and retries with `save`.

      Use case ends.

#### UC02: Edit player details

**Related user story:** US07

**MSS**

1. Coach requests to find a player by name.
2. CouchCoach displays the matching players.
3. Coach identifies a player from the displayed list and submits a new name,
   phone number and/or jersey number for them.
4. CouchCoach updates the selected player's details, keeping their other
   details, saves the updated data, and displays the updated details. The
   displayed list stays filtered.

   Use case ends.

**Extensions**

* 1a. Coach requests to list all players instead.

  Use case resumes at step 2, displaying all players.

* 1b. The search request has no keywords.

    * 1b1. CouchCoach displays an error and leaves the displayed list unchanged.

      Use case resumes at step 1.

* 2a. No players match the search.

  Use case ends.

* 3a. The request does not identify a player in the displayed list.

    * 3a1. CouchCoach displays an error and leaves the data unchanged.

      Use case resumes at step 3.

* 3b. The edit request contains invalid details, repeats a detail, or does
  not specify any details to change.

    * 3b1. CouchCoach explains the input error and leaves the data unchanged.

      Use case resumes at step 3.

* 3c. The requested name duplicates another player's name under the
  duplicate-name rule.

    * 3c1. CouchCoach reports the duplicate name and leaves the data unchanged.

      Use case resumes at step 3.

* 3d. The Coach requests to remove the player's jersey number.

    * 3d1. CouchCoach removes the jersey number, saves the updated data, and
      states that the jersey number was removed.

      Use case ends.

* 3e. The submitted details are the same as the player's current details.

    * 3e1. CouchCoach reports that no changes were made.

      Use case ends.

* 4a. The new jersey number is already used by other players.

    * 4a1. CouchCoach keeps the change and warns the Coach, naming every
      other player with that number, including players not in the displayed list.

      Use case ends.

* 4b. The new name looks like another player's name once spaces and
  punctuation are ignored.

    * 4b1. CouchCoach keeps the change and warns the Coach, naming the
      player with the similar name.

      Use case ends.

* 4c. The edited player no longer matches the search.

    * 4c1. CouchCoach removes the player from the displayed list.

      Use case ends.

* 4d. CouchCoach cannot save the updated data.

    * 4d1. CouchCoach informs the Coach that the edits were applied in the
      current session but could not be saved and may be lost after closing.

      Use case ends.

#### UC03: Categorise a player

**Related user story:** US08

**MSS**

1. Coach requests to find a player by name.
2. CouchCoach displays the matching players.
3. Coach identifies a player from the displayed list and supplies the custom
   labels that should replace that player's categories.
4. CouchCoach replaces the categories, preserves the other player details,
   saves the updated data and confirms the updated categories.

   Use case ends.

**Extensions**

* 1a. The search request has no keywords.

    * 1a1. CouchCoach displays an error and leaves the displayed list unchanged.

      Use case resumes at step 1.

* 2a. No players match the search.

  Use case ends.

* 3a. The request does not identify a player in the displayed list.

    * 3a1. CouchCoach displays an error and leaves the data unchanged.

      Use case resumes at step 3.

* 3b. The label-assignment request is incomplete or malformed.

    * 3b1. CouchCoach explains the input error and leaves the data unchanged.

      Use case resumes at step 3.

* 3c. Coach requests to clear all categories instead of supplying labels.

    * 3c1. CouchCoach removes all categories, preserves the other player details,
      saves the updated data and confirms the removal.

      Use case ends.

* 3d. The requested categories and their spellings are unchanged.

    * 3d1. CouchCoach reports that no details changed.

      Use case ends, subject to the save-failure extension below.

* 4a. CouchCoach cannot save the updated data.

    * 4a1. CouchCoach informs the Coach that the category changes were applied
      in the current session but could not be saved and may be lost after closing.

      Use case ends.

* 4b. The updated player no longer matches the active category filter.

    * 4b1. CouchCoach keeps the filter and updates the displayed matches.

      Use case ends.

#### UC04: Rate a player

**Related user story:** US12; rating comparison uses US17 sorting.

**Precondition:** CouchCoach has loaded the team.

**MSS**

1. Coach requests the roster or a subset of players.
2. CouchCoach displays those players.
3. Coach identifies a player by their displayed index and provides a whole-number rating from 0 to 10.
4. CouchCoach records the rating, preserves the player's other details, saves the team and reports the recorded rating. Any active rating sort reflects the new score.

   Use case ends.

**Extensions**

* 2a. The view contains no players.

  Use case ends; Coach may request another view.

* 3a. The request is malformed or does not identify a player in the displayed list.

    * 3a1. CouchCoach displays the format or invalid-index error and leaves player data unchanged.

      Use case resumes at step 3.

* 3b. The rating is missing, has extra arguments, or is not a supported score or removal keyword.

    * 3b1. CouchCoach explains the syntax or rating requirements and leaves the team unchanged.

      Use case resumes at step 3.

* 3c. Coach requests removal of the rating using `unrated` instead of a score.

    * 3c1. CouchCoach records Unrated and saves, or reports that the player is already unrated. A score of 0 is not used to represent Unrated.

      Use case ends.

* 4a. CouchCoach cannot save the updated team.

    * 4a1. CouchCoach reports that the change was applied in the current session but could not be saved and may be lost after closing.

      Use case ends.

#### UC05: Record a selection decision

**Related user stories:** US06, US10, US18

**MSS**

1. Coach requests to list all players.
2. CouchCoach displays the player list, showing each player's selection decision.
3. Coach requests to mark a specific player in the displayed list as kept, cut or pending (undecided).
4. CouchCoach records the decision, saves the updated team, and confirms the player's new decision.
   The displayed list keeps its current view.

   Use case ends.

**Extensions**

* 1a. Coach instead requests a narrowed or reordered list, as in UC01 extension 1a.

    * 1a1. CouchCoach displays that list.

      Use case resumes at step 2.

* 2a. The displayed list is empty.

  Use case ends.

* 3a. The request does not contain exactly one whole-number index, or the index does not identify a player in the
  displayed list.

    * 3a1. CouchCoach shows the correct command format or that the player index is invalid, and leaves the team unchanged.

      Use case resumes at step 3.

* 3b. The player already has the requested decision.

    * 3b1. CouchCoach informs the Coach that the player already has that decision, and leaves the team unchanged.

      Use case ends.

* 4a. The displayed list is the kept squad, and the new decision is cut or pending.

    * 4a1. CouchCoach removes the player from the displayed list, and the players below are renumbered.

      Use case ends.

* 4b. CouchCoach cannot save the updated team.

    * 4b1. CouchCoach informs the Coach that the decision was recorded but could not be saved, and will be lost
      if the app is closed before the Coach fixes storage and retries with `save`.

      Use case ends.

* \*a. At any time, the Coach requests to view the kept squad.

    * \*a1. CouchCoach displays only the players marked as kept, and how many there are.

      Use case ends.

#### UC06: Add a player remark

**Related user story:** US21

**MSS**

1. Coach requests to find a player by name.
2. CouchCoach displays the matching players.
3. Coach identifies a player from the displayed list and submits a free-text
   remark about that player.
4. CouchCoach records the remark for the selected player, saves the updated
   data, and displays the recorded remark.

   Use case ends.

**Extensions**

* 1a. The search request has no keywords.

    * 1a1. CouchCoach displays an error and leaves the displayed list unchanged.

      Use case resumes at step 1.

* 2a. No players match the search.

  Use case ends.

* 3a. The request does not identify a player in the displayed list.

    * 3a1. CouchCoach displays an error and leaves the data unchanged.

      Use case resumes at step 3.

* 3b. The remark request is incomplete or malformed.

    * 3b1. CouchCoach explains the input error and leaves the data unchanged.

      Use case resumes at step 3.

* 4a. CouchCoach cannot save the updated data.

    * 4a1. CouchCoach informs the Coach that the remark was recorded in the
      current session but could not be saved and may be lost after closing.

      Use case ends.

### Non-Functional Requirements

These are acceptance targets, not claims that every platform, performance or display-scale combination has been measured. Record the OS, Java version, dataset and results when validating them. Automatic saving is functional behavior covered by US06 and the saving tests; a failed save cannot guarantee durable data.

1. **Compatibility:** The packaged application should run on Windows, Linux and macOS with the supported Java 25 installation described in the User Guide. Verify startup and core commands on each platform; do not assume every newer Java release is compatible.
1. **Responsiveness:** With up to 1000 valid player records loaded, each ordinary command should display its result within 2 seconds on a computer meeting its OS's minimum requirements. Measure from command submission to the updated result/list, excluding application startup and externally blocked disk I/O.
1. **Keyboard usability:** After launching the app, a coach should be able to add, find, categorise, rate and select players, obtain help and exit through the command box without opening a menu. Check the documented normal workflow with keyboard input.
1. **Single-user operation:** The application should be usable by one coach on one computer. Simultaneous processes editing the same data file are outside the supported operating conditions.
1. **Offline operation and privacy:** Core roster-management commands should work with the network disconnected and should not transmit player data to a remote server. Opening external documentation links may require a connection.
1. **Storage portability:** Player records should use a local, readable JSON text file without a database server. A valid saved roster should reload with its field values intact. Manual file editing still has to satisfy the model's validation rules.
1. **Distribution:** The application should be delivered as one JAR of at most 100 MB and run without an application installer, once the required Java runtime is installed.
1. **Display usability:** At 1920×1080 with 100% and 125% scaling, command entry, feedback and player details should remain readable without clipped controls. At 1280×720 with 150% scaling, commands and list scrolling should remain usable. Include long tags, names and remarks in these checks.

### Glossary

* **CLI (Command Line Interface)**: A text-based interface in which the user types commands to interact with the app
* **GUI (Graphical User Interface)**: The visual interface of the app, which displays the player list and command results
* **Coach**: The user of CouchCoach, who manages the players of a sports team
* **Player**: A member or prospective member of the Coach's team whose details are recorded in CouchCoach
* **Roster**: All player records stored by CouchCoach, including players hidden by the current view
* **Displayed list**: The roster view selected by the latest `list`, `find`, `filter` or `squad`, ordered by the active rating sort or insertion order. Help temporarily replaces the visible cards without changing the underlying player view
* **Index**: A player's position in the displayed list, starting from 1. It can change after filtering, sorting or modifying players; it is not a permanent player identifier
* **Duplicate name**: A player name that matches an existing player's name after trimming leading and trailing spaces, collapsing repeated internal spaces, and ignoring letter case
* **Tag**: A custom player category, such as a position, role or trial group. Labels are normalized for whitespace and compared ignoring case; a tag is independent of selection or injury status
* **OR matching**: A player matches if any requested name keyword or any requested tag matches; the player is still listed only once
* **Rating**: An integer score from 0 to 10. _Unrated_ means no score has been assigned and is distinct from a score of 0
* **Selection status**: A player's _pending_ (undecided), _kept_ (selected) or _cut_ (not selected) decision
* **Squad**: The roster view containing all players whose selection status is kept
* **Injury status**: A player's _fit_ or _injured_ status, independent of selection status and rating
* **Remark**: A free-text note that the Coach records about a player to retain qualitative observations
* **JSON**: The plain-text data format used for local player storage; changing it by hand requires valid syntax and valid player fields

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Copy `couchcoach.jar` into an empty folder that you can write to.

   1. Open a terminal in that folder and run `java -jar couchcoach.jar` with Java 25.<br>
      Expected: The GUI opens with a set of sample players. The window size may not be optimal. A `preferences.json` file and a log file appear in the folder; the `data` folder appears after the first change to player data.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app with `java -jar couchcoach.jar` from the same folder.<br>
       Expected: The most recent window size and location are retained.

1. Launching from another working directory

   1. Open a terminal in a different folder and run the JAR using its full path, e.g. `java -jar ~/CouchCoach/couchcoach.jar`.<br>
      Expected: The sample players appear instead of your team, because the team file is read from `data/addressbook.json` in the terminal's current folder.

### Adding a player

Use a disposable folder. Run `clear` first, then `add n/Nur'ain Binte Hassan p/91112222 j/7`.

1. Adding a player with valid details

   1. Test case: `add n/Tan Wei Ming p/91234567`<br>
      Expected: `Tan Wei Ming` appears at the bottom of the list as Pending and Unrated, with no jersey number. The result box shows `New player added: Tan Wei Ming; Phone: 91234567`.

   1. Test case: `add p/+65 9876-5432 n/Muthu   s/o Ramasamy j/10 t/Striker`<br>
      Expected: The player is added as `Muthu s/o Ramasamy` with phone `+6598765432`, jersey number 10 and the Striker tag. Parameter order does not matter, repeated spaces in the name are collapsed, and spaces and hyphens in the phone are removed.

   1. Test case: `add n/José García p/(65) 8123.4567`<br>
      Expected: The player is added with phone `6581234567`. Accented letters, brackets and dots are accepted.

1. Boundary values

   1. Test case: `add n/Zero Keeper p/123 j/0`, then `add n/Ninety Nine p/123456789012345 j/99`<br>
      Expected: Both are added. A 3-digit and a 15-digit phone number, and jersey numbers 0 and 99, are accepted.

   1. Test case: `add n/Leading Zero p/91230000 j/08`<br>
      Expected: The player is added with `Jersey: 8`.

   1. Test case: a name of exactly 100 characters, then the same name with one more letter<br>
      Expected: The first is added. The second is rejected with `Names should not exceed 100 characters.`

1. Duplicate players and shared jersey numbers

   1. Test case: `add n/tan  WEI ming p/81112222`<br>
      Expected: No player is added. The result box shows `This player already exists in the team.`

   1. Test case: `add n/TanWei Ming p/90000000`<br>
      Expected: The player is added. The result box also shows `Warning: this name looks like Tan Wei Ming. Check that this is not the same player.`

   1. Test case: `find Tan`, then `add n/Lim Wei Jie p/98765432 j/7`<br>
      Expected: The full list is shown again, with `Lim Wei Jie` at the bottom. The result box also shows `Warning: jersey number 7 is also used by Nur'ain Binte Hassan.`, although Nur'ain was hidden by the search.

   1. Test case: `add n/Lim Kai Jie p/98765432 j/7`<br>
      Expected: The player is added, as phone numbers may repeat. The warning names both `Nur'ain Binte Hassan` and `Lim Wei Jie`.

1. Adding a player with invalid details

   1. Test case: `add n/Amy p/12`, `add n/Amy p/1234567890123456`, `add n/Amy p/9123ABCD`<br>
      Expected: No player is added. The result box shows the phone number rules.

   1. Test case: `add n/Amy p/91234567 j/100`, then `j/010`, `j/-1`, `j/7.5` and an empty `j/` in place of `j/100`<br>
      Expected: No player is added. The result box shows `Jersey numbers should be whole numbers from 0 to 99, e.g. 7 or 23.`

   1. Test case: `add n/Amy n/Bob p/91234567`<br>
      Expected: No player is added. The result box says that `n/` can be given only once.

   1. Test case: `add n/Amy p/91234567 r/Strong left foot`<br>
      Expected: No player is added. The result box says that `add` cannot set a remark or injury status.

   1. Other incorrect add commands to try: `add`, `add n/Amy`, `add n/ p/91234567`, `add n/Amy @ Home p/91234567`, `add n/Amy p/91234567 e/amy@x.com`<br>
      Expected: No player is added, and an error message is shown.

1. Persistence

   1. Close CouchCoach with `exit`, then relaunch it from the same folder.<br>
      Expected: The players added above are listed with the same details. In `data/addressbook.json`, players with a jersey number have a `jerseyNumber` field, and players without one have none.

Cleanup: delete the disposable folder.

### Editing a player

Use a disposable folder. Run `clear`, then `add n/Tan Wei Ming p/81234567 j/10` and `add n/Nur'ain Binte Hassan p/98765432 j/7`.

1. Editing names, phone numbers and jersey numbers

   1. Test case: `edit 1 p/9123 4567`<br>
      Expected: The result box shows `Edited player: Tan Wei Ming; Phone: 91234567; Jersey: 10`.

   1. Test case: `edit 1 p/91234567`<br>
      Expected: `No changes made: Tan Wei Ming already has these details.`

   1. Test case: `edit 1 n/TAN WEI MING`<br>
      Expected: The name changes to `TAN WEI MING`. Changing only the capitalisation of a player's own name is not a duplicate.

   1. Test case: `edit 1 n/Nur'ain Binte Hassan`<br>
      Expected: No change. The result box shows `This player already exists in the team.`

1. Jersey numbers and the duplicate warning

   1. Test case: `edit 1 j/7`<br>
      Expected: The jersey number changes to 7. The result box also shows `Warning: jersey number 7 is also used by Nur'ain Binte Hassan.`

   1. Test case: `edit 1 p/90001111`<br>
      Expected: The phone number changes, and the jersey warning is not repeated.

   1. Test case: `edit 1 j/`, then `edit 1 j/` again<br>
      Expected: The first removes the jersey number and the result box ends with `(jersey number removed)`. The second shows `No changes made: ...`.

1. Details that edit keeps

   1. Prerequisites: `keep 1`, `rate 1 8`, `remark 1 r/Strong left foot`, `injury 1 i/injured`.

   1. Test case: `edit 1 n/Tan Wei Ming p/91112222 j/23`<br>
      Expected: The card shows the new name, phone and jersey number, still with Kept, Injured, `Rating: 8/10` and the remark.

1. Editing a filtered list

   1. Test case: `find Tan`, then `edit 1 n/Ali Rahman`<br>
      Expected: The edit succeeds and the player drops out of the displayed list, which now shows 0 players. `list` shows them again.

1. Invalid edits

   1. Test case: `edit 0 n/Amy`, then `edit 99 n/Amy`<br>
      Expected: `The player index provided is invalid.` No player changes.

   1. Test case: `edit 1`<br>
      Expected: `At least one field to edit must be provided.`

   1. Other incorrect edit commands to try: `edit x n/Amy`, `edit 1 n/Amy n/Bob`, `edit 1 j/7 j/8`, `edit 1 j/100`, `edit 1 r/note`<br>
      Expected: An error message is shown, and no player changes.

1. Persistence

   1. Close CouchCoach with `exit`, then relaunch it from the same folder.<br>
      Expected: The edited details remain.

Cleanup: delete the disposable folder. Tag-specific edit checks are in [Custom player tags (US08): manual regression checks](#custom-player-tags-us08-manual-regression-checks).

### Listing all players (F2 / US02)

Run these checks in a separate folder using disposable data. Prepare the team in this order:

```text
clear
add n/Tan Wei Ming p/91234567
add n/Lim Wei Jie p/92345678
add n/Nur Aisyah p/93456789
```

For every successful listing below, the status message is exactly `Listed all players`,
including when the team is empty. Listing clears the current name/tag/injury/squad
filter and rating comparator, displaying all remaining players in insertion order
with indices starting from 1.

| Test case | Expected result |
| --- | --- |
| `list` | Tan, Lim, and Nur displayed in that order, numbered 1, 2, and 3, with their recorded names and phone numbers. |
| `find wei`, then `list` | The two search results are replaced by all three players in their original order. |
| `find NobodyMatchesThisName`, then `list` | The empty search results are replaced by all three players. |
| `rate 2 10`, `sort rating`, then `list` | Lim is first when sorted; list restores Tan, Lim, Nur. |
| `keep 2`, `squad`, then `list` | Lim is the only squad entry; list restores all three in insertion order. |
| `injury 2 i/injured`, `filter i/injured`, then `list` | The injury-filtered view is replaced by all three players. |
| `list` twice | Both requests succeed; the order and indices stay the same, with no duplicate players. |
| `list all` or `list n/Tan` | Trailing arguments are ignored; all three players are displayed. |
| `list` with leading and trailing spaces | All three players are displayed, as with `list`. |
| `find wei`, then `List` or `lis` | `Unknown command. Type 'help' to see the list of commands.` is shown; Tan and Lim remain displayed, numbered 1 and 2. Run `list` to restore all three players. |
| `help`, then `list` | The help panel is replaced by the full player list. |
| `delete 2`, then `list` | Tan and Nur are displayed in that order, numbered 1 and 2; Lim is not restored. |
| `clear`, then `list` | The player panel is empty; listing still succeeds with `Listed all players`. |

Before each listing check, record the player data file's contents and modification
time. Both must remain unchanged after successful and rejected listing requests;
`list` changes only the displayed list. The setup, `delete`, and `clear` commands
do change player data, so take a new baseline after those commands.

To check listing with unavailable storage, prepare the three players again and run
`find wei`. While the app remains open, rename the `data` directory and create a
regular file named `data` in its place, blocking directory creation. Run `list`.
Expected: all three players appear in their original order, `Listed all players`
is shown, and no save-failure message appears. Remove the temporary file and restore
the directory before modifying players or closing the app.

### Ratings and sorting (US12 / US17): manual regression checks

Use Java 25 and a candidate JAR built from the version under review. Put it in a
**separate disposable working directory**, launch with `java -jar couchcoach.jar`,
and record the commit/JAR identity. These instructions describe the current command
syntax `sort rating [all|squad] [asc|desc]`, including current-view and explicit scopes.

Prepare this fixture in insertion order. Re-run the complete setup before each
independent table row; commands within a row are sequential. Setup changes player data.

```text
clear
add n/Amy Zero p/123 j/7 t/Trial
add n/Bob Ten p/456 t/Trial
add n/Cy Tie p/789 t/Other
add n/Dee Unrated p/234 t/Trial
add n/Eli Unrated p/567 t/Other
rate 1 0
rate 2 10
rate 3 10
keep 1
keep 2
keep 3
remark 1 r/Left foot
injury 1 i/injured
```

#### Rating values, errors and preservation

| Test case | Expected result |
| --- | --- |
| `rate 1 0`, then `rate 1 10` | Amy is rated 0/10 and then 10/10; each success identifies Amy and the new score. |
| `rate 1 unrated`, repeat it, then `rate 1 UNRATED` | First: `Removed the rating of player: Amy Zero; Rating: Unrated`. Repeats: `Amy Zero is already unrated.` |
| `rate 1 0` versus Dee's unchanged rating | Amy displays 0/10; Dee displays Unrated. They are distinct states. |
| `rate 1 -1`, `rate 1 11`, `rate 1 8.0`, `rate 1 +8`, `rate 1 08`, `rate 1 r/8`, `rate 1 unrate` | Rating-constraint feedback with the `rate INDEX unrated` removal hint; no player/view/file change. |
| `rate`, `rate 1`, `rate 1 8 extra` | Format error with rate syntax; no change. |
| `rate 0 8`, `rate -1 8`, `rate 2147483648 8`, `rate 6 8` | `The player index provided is invalid.`; no change. |
| `rate 1 8`, then inspect Amy and restart | Name, phone 123, jersey 7, Trial tag, kept status, Left foot remark and injured status are preserved; rating 8 survives restart. |
| `rate 1 unrated`, then restart | Amy remains Unrated; unrelated fields survive. |

Check successful numeric feedback exactly, e.g. `Rated player: Amy Zero; Rating: 0/10`.
After each rejected request compare both the displayed names/indices and saved file
bytes with the baseline taken **after setup**. A successful rate command may save
even when it reports already-unrated; do not assert unchanged modification time for it.

#### Order, active views and displayed indices

Abbreviations below use the fixture players' first names.

| Test case | Expected result |
| --- | --- |
| `sort rating`, then `sort rating desc` | Bob, Cy, Amy, Dee, Eli both times; descending feedback. |
| `sort Rating ASC` | Amy, Bob, Cy, Dee, Eli; ascending feedback. Zero precedes both Unrated players; Bob stays before Cy. |
| `sort rating desc`, `sort rating asc`, `sort rating desc` | Direction changes preserve insertion-order ties; the final order is Bob, Cy, Amy, Dee, Eli. |
| `filter t/Trial`, `sort rating` | Bob, Amy, Dee only; Cy and Eli remain hidden. |
| `filter i/injured`, `sort rating asc` | Amy only, still injured and kept. |
| `find Dee`, `sort rating all` | The search is replaced by Bob, Cy, Amy, Dee, Eli. Feedback names all players and descending order. |
| `filter t/Trial`, `sort rating ALL ASC` | Full roster, Amy, Bob, Cy, Dee, Eli; ascending feedback names all players. |
| `list`, `pending 2`, `find Dee`, `sort rating squad` | Bob is pending and excluded. All kept players are restored: Cy, Amy; feedback names kept players and descending order. |
| `find Dee`, `sort rating asc squad` | Scope/direction order is accepted. All kept players appear: Amy, Bob, Cy. |
| `sort rating squad`, `find Unrated` | The name search replaces the squad filter; pending Dee and Eli appear in insertion order. The comparator remains active. |
| `list`, `pending 1`, `pending 2`, `pending 3`, `sort rating squad` | Empty kept-player view; no error. |
| `sort rating all squad`, `sort rating asc desc`, `sort rating all all`, `sort rating squad squad` | Format error before changing the predicate or comparator. Previous view/data unchanged. |
| `find Ten`, `sort rating`, `find Unrated` | First Bob only; the later search replaces the old filter and shows Dee then Eli. |
| `squad`, `sort rating` | Bob, Cy, Amy only. Sorting does not add pending players. |
| `sort rating`, `find Unrated`, `squad` | Squad replaces the search and restores all kept players in active rating order: Bob, Cy, Amy. |
| `sort rating`, `edit 1 p/999` | Bob's phone changes; order and scores stay Bob, Cy, Amy, Dee, Eli. |
| `sort rating`, `rate 1 0` | Bob is the target. New order: Cy, Amy, Bob, Dee, Eli; Amy precedes Bob in the zero-score tie. |
| `sort rating`, `rate 1 unrated` | Bob becomes Unrated. New order: Cy, Amy, Bob, Dee, Eli; Bob precedes the later-added Unrated players. |
| `sort rating`, `delete 1`, `list` | Bob is deleted; list shows Amy, Cy, Dee, Eli in insertion order. |
| `filter t/Trial`, `sort rating`, `add n/Fay New p/678` | Full roster returns, retaining the sort: Bob, Cy, Amy, Dee, Eli, Fay. Fay is Unrated. |
| `filter t/Trial`, `sort rating`, `edit 1 n/Bob Changed` | Bob's details change, current tag filter and sort stay active: Bob, Amy, Dee. |
| `filter t/Trial`, `sort rating`, `tag 1 t/Other` | Bob gains Other and keeps Trial; Bob, Amy, Dee remain in active rating order. |
| `sort rating`, then `list all` | Full insertion order Amy, Bob, Cy, Dee, Eli; `Listed all players`. Sorting is cleared. |
| `sort`, `sort name`, `sort rating up`, `sort rating asc extra` | Format error with sort usage. Previous displayed view and order unchanged. |
| `clear`, `sort rating`, `sort rating asc`, `sort rating all asc`, `sort rating squad`, `list` | Empty view; all sorts succeed and list reports `Listed all players`. |
| `sort rating`, then restart from the same directory | Amy, Bob, Cy, Dee, Eli in insertion order; ratings 0, 10, 10, Unrated, Unrated persist. The previous filter/sort does not. |

For a sort/list-only row, record the file's bytes and modification time after setup;
neither should change. Repeat `filter t/Trial`, `sort rating`, `list` with storage
blocked by renaming `data` and placing a regular file named `data` at that path.
All three view commands should still succeed. Remove that blocker and restore the
directory before a mutation or restart. To check rate's save failure, use the same
blocker and run `rate 1 8`: Amy changes on screen, but the save-failure feedback
appears with the applied rating feedback and the command box clears. Restore storage
and run `save`; wait for `Saved the current team to disk.` before testing restart. Confirm both Amy's new rating and all preserved fields in the reloaded file.

**Cleanup:** close the app, restore any renamed directory, and remove only the
disposable test directory. Keep any recorded evidence outside the real team's data.
Do not run `clear` against a coach's working roster.

### Finding players by name (F3 / US04)

Run these checks in a separate folder using disposable data. Prepare the team in this order:

```text
clear
list
add n/Tan Wei Ming p/91234567
add n/Lim Wei Jie p/92345678
add n/Nur Aisyah p/93456789
add n/Muthu s/o Ramasamy p/94567890
```

The name character set permits the slash-containing record needed by F3,
as well as apostrophes, hyphens, and periods.

| Test case | Expected result |
| --- | --- |
| `find wei` | Tan and Lim displayed in that order, numbered 1 and 2; `2 players match the current command: find wei`. |
| `find TAN` | Tan only, numbered 1; `1 player matches the current command: find TAN`. |
| `find wei nur` | Tan, Lim, and Nur once each; `3 players match the current command: find wei nur`. |
| `find ming tan ming` | Tan once; `1 player matches the current command: find ming tan ming`. |
| `find We` | Empty panel; `0 players match the current command: find We` (no partial matching). |
| `find 91234567` | Empty panel; phone numbers are not searched. |
| `find S/O` | Muthu only; `1 player matches the current command: find S/O`. |
| `find s` | Muthu only; `1 player matches the current command: find s` (a part of `s/o` between punctuation matches). |
| `find n/Wei` | Empty panel; the slash keyword is literal, not a name prefix. |
| `find` or `find` followed only by spaces | Exact four-line format error documented in the UG; previous displayed list unchanged. |
| `Find wei` | Unknown-command error; previous displayed list unchanged. |
| `find   wei   ming` | Same matches as the equivalent command with single spaces. |
| `find tan` followed by `find nur` | Nur appears despite being hidden by the first search. |
| `filter i/injured`, then `find nur` | Nur appears even if the injury filter was empty; the name search considers the full roster. |
| `list`, `keep 3`, `squad`, then `find wei` | Tan and Lim appear even though neither is kept; `find` replaces the squad view. |
| `list`, `rate 1 2`, `rate 2 9`, `sort rating`, then `find wei` | Lim is displayed as 1, Tan as 2; the search preserves the active descending sort. |
| `tag 1 t/Captain` after the previous row, then `filter t/Captain` | Lim gains Captain and remains the sole match; Tan is unchanged. Filter replaces name search while retaining sorting. |
| `list` after a search | All four players displayed in original order. |
| `find wei`, then `delete 1` | Tan removed; Lim remains numbered 1; Nur and Muthu stay hidden. |
| `delete 1` again | Lim removed; filtered panel empty even though Nur and Muthu remain in the team. |
| `list`, `clear`, then `find wei` | Empty panel; `0 players match the current command: find wei`; no failure on an empty team. |

Recreate the four-player fixture above before the storage checks. Before running successful and invalid searches, record the data file's contents and
modification time. Both must remain unchanged after every search. To check searches
with unavailable storage, rename the data directory after the team is loaded and
create a regular file named `data` in its place, blocking directory creation. Execute `find wei` and verify that
the normal search feedback still appears. Remove the temporary file and restore the
directory before adding or deleting players. Relaunch after preparing Muthu's record
and confirm that `find s/o` still finds it, checking that the slash survives storage.

After the table and storage checks, add disposable records `Tan Wei-Ming`, `Shaun O'Neil` and `Lim Jr.`. Verify `find Ming`, `find Neil` and `find Jr` find the appropriate records, while `find Min` does not.

Then add `Shaun ((O'Neil))`, `Tan Wei-Ming, Jr.`, `David(lee)` and `David(l(e(e)))`, and check punctuation at word edges:

| Test case | Expected result |
| --- | --- |
| `find O'Neil`, then `find (O'Neil)`, then `find ((O'Neil))` | Each lists Shaun O'Neil and Shaun ((O'Neil)). |
| `find Wei-Ming` | Tan Wei-Ming and Tan Wei-Ming, Jr. |
| `find (lee)` | David(lee) only. |
| `find lee`, then `find l(e(e))` | `lee` lists David(lee) only; `l(e(e))` lists no players, as parts are never joined. |
| `find ()` | No players are listed. |

After all checks, close the app and remove the disposable folder.

See [Name search implementation](#name-search-implementation-f3-us04) for the parsing and predicate design.

### Checking find/filter/squad feedback after edits and deletions

Use a disposable data directory and Java 25. Start with `clear`, then add
`add n/Amy One p/111 t/Trial` and `add n/Amy Two p/222 t/Trial`.

| Commands | Expected result |
| --- | --- |
| `find Amy` | Both players appear; `2 players match the current command: find Amy`. |
| `edit 1 n/Bob` | `Edited player: Bob; Phone: 111; Tags: Trial`, followed on a new line by `1 player matches the current command: find Amy`. Bob leaves the view. |
| `delete 1` | Amy Two's deleted details, followed by `0 players match the current command: find Amy`. The panel is empty. |
| `delete 1`, then `filter t/` | The invalid-index and empty-tag errors respectively; no player changes. |
| `filter t/Trial i/fit`, then `edit 1 p/333` | Bob's edited details, followed by `1 player matches the current command: filter t/Trial i/fit`. |
| `sort rating`, `help`, then `edit 1 p/444` | The player panel returns. Bob's edited details still end with the same filter summary and count. |
| `keep 1`, `squad`, then `edit 1 p/555` | Bob's edited details, followed by `1 player in your squad`. |
| `delete 1` | Bob's deleted details, followed by `0 players in your squad`. |
| `list`, `add n/Charlie p/666`, `find Charlie`, then `add n/Dana p/777` | The add restores both players. A subsequent `edit 1 p/888` has ordinary edit feedback without a view summary. |
| `find Charlie`, `sort rating all`, then `delete 2` | Dana is deleted; ordinary deletion feedback has no stale find summary. |
| `keep 1`, `sort rating squad`, then `edit 1 p/999` | Charlie's edited details, followed by `1 player in your squad`. |

Also try a find or filter with repeated spaces and pasted tabs. Feedback echoes
the command with single spaces while preserving case and condition order. Check
zero, one and multiple matches. A failed command must not replace the previous
view description. An unchanged edit retains its existing no-change feedback.
When an edit produces a duplicate-jersey or similar-name warning, that warning
remains above the new summary. Repeat an edit and deletion with blocked storage:
the save warning follows the updated view summary, and `save` can retry storage.

The model stores the selecting command alongside its filter. Find and filter
parsers supply the command text; squad and explicitly squad-scoped sorting use
`squad`. Updating a filter without a description clears it, as required by
`list`, adding a player and all-scoped sorting. Edit, delete, tag and status commands format feedback
after the observable list has refreshed, so a player leaving the view is not
counted. The description is session state and is not written to the data file.

For tag clearing and squad status changes, reset the disposable fixture with
`clear`, `add n/Amy p/111 t/Trial`, `add n/Bob p/222 t/Trial`, `keep 1`, and `keep 2`.
Run these rows in order:

| Commands | Expected result |
| --- | --- |
| `filter t/Trial i/fit`, then `tag 1 t/` | `Amy: tags cleared.`, followed by `1 player matches the current command: filter t/Trial i/fit`. Only Bob remains visible. |
| `tag 1 t/` | `Bob: tags cleared.`, followed by `0 players match the current command: filter t/Trial i/fit`. The panel is empty; both players remain in the team. |
| `tag 1 t/` again | Invalid-index error; no player changes. |
| `squad`, `sort rating`, then `pending 1` | `Marked Amy as pending.`, followed by `1 player in your squad`. Bob becomes the first displayed player. |
| `cut 1` | `Marked Bob as cut.`, followed by `0 players in your squad`. Both records remain in the team. |
| `pending 1` or `cut 1` again | Invalid-index error; no player changes. |

Also check a squad with at least three kept players to verify plural counts.
Repeat the tag clear and status change with blocked storage: each refreshed count
must appear before the save warning. Restore storage, run `save`, and restart to
verify that tags and decisions persist. Invalid syntax, invalid indices and
already-set/no-change results keep their existing feedback. Successful tag
additions and status changes in name or injury filters also append the current
view summary, even when the number of displayed players is unchanged.

### Deleting a player

**Setup:** launch CouchCoach from an empty folder, so that it starts with the six sample players, then run `list`.
Run `rate 3 3` and `rate 4 9`, so that Charlotte Oliveiro is rated 3 and David Li is rated 9.

| Test case | Expected result |
| --- | --- |
| `delete 1` | The first player is removed. The result box shows `Deleted player:` followed by their details. The others move up one number. |
| `find Bernice`, then `delete 1` | Bernice Yu is removed. The search stays active, so the list is now empty. Run `list` afterwards. |
| `sort rating`, then `delete 1` | David Li (rated 9) is removed, not Charlotte Oliveiro, who comes first in insertion order. The list stays sorted. |
| `delete 0`, `delete -1`, then `delete 99` | Each shows `The player index provided is invalid.` and removes nobody. |
| `delete`, `delete x`, then `delete 1 2` | Each shows `Invalid command format!` with the `delete` usage, and removes nobody. |
| `squad` (no one kept yet), then `delete 1` | The squad is empty, so the index is invalid and nobody is removed. |
| Close and relaunch from the same folder. | The deleted players are still gone. |

**Cleanup:** delete the folder.

### Marking selection decisions and viewing the squad

**Setup:** launch from an empty folder (six sample players, all **Pending**) and run `list`.

| Test case | Expected result |
| --- | --- |
| `keep 1` | `Marked Alex Yeoh as kept.` The first card's badge changes to **Kept**. |
| `keep 1` again | `Alex Yeoh is already marked as kept.` Nothing changes. |
| `cut 2`, then `pending 2` | The second player becomes **Cut**, then **Pending** again. |
| `keep 0`, `keep 7`, `keep`, `keep a`, `keep 1 2` | `0` and `7` show `The player index provided is invalid.`; the others show the `keep` usage. No badge changes. |
| `find Bernice`, then `keep 1` | Bernice Yu becomes **Kept** and the search stays on screen. |
| `squad` | Exactly the kept players appear, and the result box shows `2 players in your squad.` |
| `cut 1` while viewing the squad | That player is cut and leaves the list; the other kept player moves up to number 1. |
| `list`, `keep 1`, `rate 1 3`, `rate 2 9`, `sort rating`, then `squad` | Only the two kept players appear, Bernice Yu (9) before Alex Yeoh (3). |
| `list`, mark every kept player `pending`, then `squad` | The list is empty and the result box suggests `keep INDEX`. |
| Close and relaunch from the same folder. | Every badge is as you left it. |

**Cleanup:** delete the folder.

### Remarks and injury status (US21 / US13): manual regression checks

In a disposable folder, prepare the fixture below before each independent row. Commands within a row run sequentially. Other Player is roster index 1; Amy is index 2 until a view changes it.

```text
clear
list
add n/Other Player p/111
add n/Amy p/123 j/7 t/Trial
keep 2
rate 2 8
```

| Action | Expected result |
| --- | --- |
| `find Amy`, `remark 1 r/Strong left foot`, then `remark 1 r/Improved passing`. | Amy's note is replaced, not appended; feedback names Amy. Other Player stays unchanged. Amy remains Kept, rated 8, jersey 7, tagged Trial and fit. |
| `find Amy`, `remark 1 r/Note`, then `remark 1 r/`. | `Updated remark for Amy: (cleared)`; the remark row disappears without a blank gap. |
| Then `remark 1 r/` again. | `No changes made: Amy already has these details.` |
| `find Amy`, then `remark 1 r/See n/coach for drills`. | The whole text is stored literally; `n/` is not a remark parameter. |
| `find Amy`, then try `remark 1`, `remark 1 r/One r/Two`, `remark 0 r/Note` and `remark 2 r/Note`. | Missing/duplicate-prefix or index errors; no player, view or file changes. |
| `find Amy`, `injury 1 i/INJURED`, repeat `injury 1 i/injured`, then `injury 1 i/fit`. | Injured badge appears, repeated status gives already-set feedback, then the badge disappears. The selection badge remains Kept throughout. |
| `find Amy`, then try `injury 1`, `injury 1 i/hurt`, `injury 1 i/fit i/injured`, and `injury 2 i/injured`. | Appropriate format/status/duplicate-prefix/index error; no player, view or file changes. |
| `injury 2 i/injured`, `filter t/Trial i/injured`, then `injury 1 i/fit`. | Amy is the displayed target and leaves the injured view. The list becomes empty; `list` restores both players with Amy fit. |
| `sort rating`, `remark 1 r/Trial note`, then `injury 1 i/injured`. | Rated Amy is targeted as displayed index 1, ahead of unrated Other Player. Rating order, selection, tags, phone and jersey remain. |
| `remark 2 r/Trial note`, `injury 2 i/injured`, exit, then relaunch. | Amy's remark and Injured badge persist; all unrelated fields remain. The launch view resets to insertion order. |

Compare the team file bytes after rejected requests. Also run the save-failure procedure below with a valid remark or injury update: the applied field remains visible, the warning instructs `save`, and recovery persists the update without repeating it.

### Clearing the roster

Use only disposable data. Start with two players, keep one, then enter `squad` and `sort rating`. `clear extra` must reject the command without deleting either player. Enter `clear`: both the visible squad player and the hidden player are removed, with no confirmation. `list` and a restart must still show an empty team. A second `clear` on that empty team succeeds. Under blocked storage, `clear` empties the in-memory roster and warns; restore storage and use `save` before restarting.

### Pasted prefix separators

Use a disposable roster. In the commands below, replace `<TAB>` with a real tab and `<NBSP>` with a real non-breaking space before pasting; these labels are not literal input. Pressing Tab in the app moves focus instead of inserting a tab.

| Action | Expected result |
| --- | --- |
| `add<TAB>n/Amy<TAB>p/123<TAB>t/Trial` | Amy is added with phone 123 and tag Trial. |
| `edit 1<NBSP>p/456`, then `tag 1<TAB>t/Captain`. | Phone changes to 456; tags become Captain. |
| `filter<TAB>t/Captain`, then `filter<NBSP>i/fit`. | Amy is found in each view. |
| `remark 1<TAB>r/Trial note`, then `injury 1<NBSP>i/injured`. | Both updates succeed and preserve other fields. |
| `add n/Amy<TAB>n/Bob p/789` | Duplicate-name-prefix error; nothing is added. |

Pasted tabs become ordinary spaces in the command box, including within a remark. For positional commands (`rate`, `sort`, `find`), ordinary spaces and pasted tabs therefore work between arguments; non-breaking spaces do not act as positional separators. Do not paste multiline commands: line breaks do not delimit prefixes.

### Viewing help

Prerequisites: Launch CouchCoach with at least one player and run `list`. Record the
players' names, phone numbers, and order before these checks. Run `list` before each
independent help entry-point check so that the player list is visible first.

| Test case | Expected result |
| --- | --- |
| Enter `help`. | The player-list area shows the **CouchCoach commands:** summary in the main window, with every current command, including `tag`, `filter`, scoped `sort rating`, `rate`, `remark`, `injury`, and `save`. Scroll to verify the final rows. The result box shows `Showing CouchCoach command summary.` No separate window opens. |
| Check help at normal and minimum window widths, scrolling through every command. | A blank line separates command entries, including `keep`, `cut`, and `pending`. Syntax and descriptions stay aligned, long descriptions wrap, and the final `exit` command remains reachable by scrolling. |
| Enter `help extra words`. | The extra words are ignored; the same summary and result message appear as for `help`. |
| Select **Help → Help** from the menu. | The same summary appears in the player-list area. The existing result-box message is unchanged. |
| Press **F1** with focus in the command box, then repeat with focus in the result box. | Each shortcut shows the same summary in the player-list area, without opening another window or changing the result-box message. If the keyboard uses media keys by default, use **Fn+F1**. |
| Enter `help` repeatedly, or select **Help → Help** repeatedly. | A single summary remains visible; no duplicate panels or windows appear. |
| With help visible, enter `addd`. | The result box shows `Unknown command. Type 'help' to see the list of commands.` The command box is marked as invalid and the help summary remains visible. |
| With help visible, submit an empty command, then a command containing only spaces. | Both produce the same unknown-command message and invalid-command indication; the help summary remains visible. |
| With help visible, enter `list`. | The help summary is replaced by all players in their original order, with unchanged names and phone numbers. The result box shows `Listed all players`. |

Repeat `help` followed by `list` on an empty team using disposable data. The summary
must still appear, and `list` must return to an empty player list successfully.
Opening help and submitting the invalid commands above must not modify player data.
If the data file already exists, record its contents and modification time before
these checks and verify that both remain unchanged afterwards.

### Saving data

Use a disposable folder for every case, and launch CouchCoach from that folder with `java -jar PATH_TO/couchcoach.jar`,
so that the team file is `data/addressbook.json` in that folder. Close the app before editing any file, and keep a copy
of the file before each case so that you can compare it afterwards.

**Saving and reloading**

| Test case | Expected result |
| --- | --- |
| Launch from an empty folder. | Six sample players appear, no warning is shown, and no `data/addressbook.json` exists yet. |
| `list`, `find Alex`, `filter t/Striker`, `sort rating`, `squad`, `help`, then `exit` | The file still does not exist: none of these save. |
| Relaunch, then `keep 1`. | `data/addressbook.json` now exists and contains all six players, the first with `"selectionStatus" : "KEPT"`. |
| `keep 1` again, then `delete 99` | The first reports that nothing changed and the second is rejected; the file's players are unchanged. |
| Close and relaunch. | The same six players appear, the first still **Kept**. |

**Missing, invalid and older data files**

| Setup (app closed) | Expected result on launch |
| --- | --- |
| Delete `data/addressbook.json`. | The sample team appears again. |
| Replace the file's contents with `{ "persons": [ {"name": "Amy"` (malformed JSON). | An empty team, and the result box shows `The data file is not in a readable format. CouchCoach has started with an empty team. Your next change or 'save' will replace the file with the current team. Back up the file before continuing.` |
| Then run `list`, `find Amy`, `squad` and `exit`. | The file's contents are unchanged. |
| Relaunch, then `add n/New Player p/123`. | The invalid file is replaced by a valid one containing only New Player. |
| Use `{"persons":[{"name":"Amy","phone":"123"},null]}`. | Empty team and the same warning; the file is unchanged until the next change. |
| Use `{}`, an empty file, `{"persons":null}`, a player with `"selectionStatus":"MAYBE"`, a player with `"phone":"9123 4567"`, or two players named `Amy Tan` and `amy  tan`. | Each gives an empty team and the same warning. |
| Use `{"persons":[{"name":"Amy Tan","phone":"91234567"}]}` (an older file without the newer fields). | Amy Tan loads as **Pending**, unrated, fit, with no tags, remark or jersey number. No warning. |
| Use a player with `"selectionStatus":"kept"` or `"injuryStatus":"INJURED"`. | The player loads normally; these values are not case-sensitive. |

**Save failure and recovery**

Prepare a fresh disposable team with `clear`, `list`, `add n/Amy p/123` and `add n/Bob p/456`. Both start pending. Close other instances using this folder. Run the rows in order; use your file manager or another terminal to rename paths while CouchCoach stays open. The blocker causes failure before the original file is written and works without relying on OS permission behaviour.

| Test case | Expected result |
| --- | --- |
| Rename the `data` folder to `data-backup` and create a regular file named `data` in its place. Enter `keep 1`. | Amy becomes **Kept**. Feedback begins `Marked Amy as kept.` and appends `Your change was applied but could not be saved to disk. Fix the problem and type 'save' to retry before closing CouchCoach, or the change will be lost. Do not repeat the original command.` The command box clears. The backup still records Pending. |
| While blocked, enter `cut 2`, then `save`. | Bob becomes **Cut** in memory. `save` reports `The current team could not be saved to disk. Fix the problem and type 'save' to retry before closing CouchCoach, or unsaved changes will be lost.` Neither command changes the backup; no mutation is repeated. |
| Enter `find Amy`, then `sort rating`, then `save` while blocked. | View commands work without a save warning. The explicit save still reports failure; both in-memory changes remain. |
| Remove the blocker file and rename `data-backup` back to `data`. Enter `save`. | `Saved the current team to disk.` Both Amy's Kept and Bob's Cut statuses are written, although only Amy is displayed. The current filter and sort remain active. |
| Enter `save extra`. | Format error; data and view stay unchanged and no write is attempted. |
| Enter `list`, close and relaunch from the same folder. | Amy remains Kept and Bob remains Cut; both appear in insertion order. |
| Delete the disposable `data` folder while the app is open, then enter `pending 2`. | The folder and file are recreated, and Bob's pending status is saved normally. |
| Close the app. Copy the JAR into a new empty folder, make that folder read-only (e.g. `chmod a-w FOLDER` on macOS/Linux), then launch from it. Enter `keep 1`. | The window opens with the sample team; the terminal notes that the log file could not be created. Alex becomes **Kept** and the feedback appends the same save warning. No file appears in the folder. Make the folder writable again before deleting it. |

Also test a destructive command on a fresh copy of the fixture: block storage, enter `delete 1` once, then restore storage and enter `save`. Amy is removed once, Bob remains, and the restored file contains only Bob. Repeating `delete 1` would incorrectly remove Bob, which is why recovery uses `save`.

Restore renamed directories and close the app before deleting disposable folders. With an invalid team file, verify separately that `save` overwrites it with the in-memory empty team, as the startup warning states; preserve a backup first. On a fresh sample roster, `save` creates the whole team file even without a player change.

#### Null data and preference recovery

Use Java 25 and a disposable launch folder. Close the app before editing files.
Keep a backup of the valid team file before each independent case.

| Setup and action | Expected result |
| --- | --- |
| Set `data/addressbook.json` to `null`, then launch. | Empty team and the unreadable-data warning; the file still contains `null`. |
| Run `list`, `help`, then `exit` after that recovery. | The invalid team file remains unchanged. |
| Relaunch, then add `add n/Recovery Player p/123`. | The new team replaces the invalid file; restarting restores Recovery Player. |
| With valid team data, set `preferences.json` to `null`, then launch. | Default window preferences, no team-data warning, and the same players; preferences are rewritten as valid JSON. |
| Repeat with `preferences.json` set to `{"guiSettings":null}`. | The same default-preference recovery; team file bytes remain unchanged. |
| Delete `data/addressbook.json`, then launch. | Sample players appear; a subsequent data change creates a valid team file. |
| Replace the team file with malformed JSON, then launch. | Empty team and the unreadable-data warning; the invalid file stays unchanged until a data change. |

### Jersey number zero padding: manual regression checks

Use a disposable empty team and run these commands in order:

| Action | Expected result |
| --- | --- |
| `add n/Amy p/123 j/099` | Amy shows Jersey #99. |
| `add n/Bob p/456 j/99` | Bob is added with a warning naming Amy for jersey 99. |
| `edit 2 j/010` | Bob shows Jersey #10; other player fields are retained. |
| `edit 2 j/0100`, `edit 2 j/+10`, `edit 2 j/10.0` | Jersey constraint error; the team and file remain unchanged. |
| `edit 2 j/000` | Bob shows Jersey #0. |
| Restart. | Amy remains #99 and Bob #0; the saved numbers have no leading zeros. |

### Custom player tags (US08): manual regression checks

Launch the candidate JAR with Java 25 in a separate writable folder. Use only disposable data. Prepare a bystander before the tagged player so a filtered index differs from the roster index:

```text
clear
list
add n/Other Player p/111
add n/Tag Tester p/123 j/7 t/Striker t/Trial Group 2
find Tag
keep 1
rate 1 8
remark 1 r/Strong left foot
injury 1 i/injured
```

The displayed list must contain only Tag Tester as 1; Other Player remains in the roster. Execute these rows in order. Unless a row says otherwise, Tag Tester remains the only displayed player, with jersey 7, rating 8, Kept, the remark and Injured. Check these fields after each successful tag update.

| Action | Expected result |
| --- | --- |
| Inspect the card and feedback, then `edit 1 p/456`. | Both tags appear alphabetically without hiding name/phone; the phone changes and the tags remain. Other Player is unchanged. |
| `tag 1 t/Captain t/captain` | Captain is added once, retaining the first spelling. Striker and Trial Group 2 remain; feedback lists Captain, Striker, Trial Group 2. The name search remains active. |
| `tag 1 t/CAPTAIN` | No changes are made; Captain keeps its saved spelling, and Striker and Trial Group 2 remain. |
| `tag 1 t/` | Tags disappear; feedback says `Tag Tester: tags cleared.`, then `1 player matches the current command: find Tag`. Other player fields remain. |
| Repeat `tag 1 t/`. | `No changes made: Tag Tester already has these details.` The view and fields remain unchanged. A successful edit result still attempts a save. |
| `tag 1 t/ Trial   Group 2` | One label, `Trial Group 2`, with collapsed whitespace and preserved capitalisation. |
| `tag 1 t/Bad!` | Specific error naming `!`; tags and the previous view stay unchanged, and the file is not written. |
| `edit 1 p/789 t/Captain` | Guidance to use `tag`; the phone stays 456, tags and the previous view stay unchanged, and the file is not written. |
| Try `tag 1 t/ t/Striker`, then `tag 1 t/Striker t/`. | Each rejects the whole command with the empty-tag message; the label remains Trial Group 2. |
| Try `tag 99 t/Captain`, `tag 0 t/Captain`, and `tag t/Captain`. | Out-of-range index gives `The player index provided is invalid.`; malformed/missing indices show index/usage guidance. No data or view changes. |
| `tag 1 t/123456789012345678901234567890` | A 30-character label is added alongside Trial Group 2. |
| `tag 1 t/1234567890123456789012345678901` | Error names the 31-character label and 30-character limit; the old 30-character label remains. |
| `tag 1 t/Défenseur t/U18 & Under t/Centre Back/Wing`, then exit, relaunch and run `find Tag`. | All five labels persist in alphabetical order: 123456789012345678901234567890, Centre Back/Wing, Défenseur, Trial Group 2, U18 & Under. The restored name filter shows only Tag Tester as 1, ready for the next row. |
| `tag 1 t/Striker`, then `filter t/Striker`, then `tag 1 t/Captain`. | Tag Tester remains visible with Striker and all earlier labels; Captain is appended. The Striker filter remains active. Run `list`: Other Player and Tag Tester appear, with all seven labels on Tag Tester. |
| `find Tag`, then `rate 1 9`, `cut 1`, `pending 1`, `keep 1`, `remark 1 r/Improved passing`, `injury 1 i/fit`, and `edit 1 j/9 p/789`. | After each command all seven tags remain; the changed field updates without dropping unrelated fields. The final player is kept, fit, rated 9, jersey 9, phone 789, with the new remark. |
| `squad`, then `tag 1 t/Striker`. | Only Tag Tester is shown, still kept; adding the existing Striker tag reports no changes and retains the squad view and all assessment fields. |
| `tag 1 t/`, then exit and relaunch from the same folder. | Tags remain cleared; all other fields persist. Both players return in insertion order because the view is not persisted. |

For each rejected update, compare the data file contents and modification time before and after: neither should change. For successful updates, inspect the JSON and reload before accepting persistence as verified. After any reload during this sequence, run `find Tag` before continuing indexed commands: startup restores insertion order, so Other Player is otherwise 1 and Tag Tester is 2. Also run the sorted-index tag-update sequence under the filter tests below.

With the app closed, back up the disposable JSON. In separate copies, omit the `tags` field and then set it to `null`: the player loads without tags. Restore the backup between cases. An invalid tag string or a null entry inside the tag array must instead trigger the unreadable-data warning. Do not modify the only copy of real data. Remove the disposable test folder after closing the app and restoring any renamed paths.

### Filtering by tag (US16): manual regression checks

Use a fresh disposable folder, or clear only the disposable roster. Prepare players in this order:

```text
clear
list
add n/Amy p/111 t/Striker t/Captain
add n/Bob p/222 t/Goalkeeper
add n/Charlie p/333 t/Captain
rate 1 2
rate 3 9
injury 1 i/injured
keep 2
keep 3
```

Before the sorting rows, all views use insertion order. Execute rows in order; `find`, `filter` and `squad` replace one another, rather than intersecting.

| Action | Expected result |
| --- | --- |
| `filter t/STRIKER` | Amy only as 1; `1 player matches the current command: filter t/STRIKER`. |
| `filter t/Striker t/Captain t/striker` | Amy and Charlie once each, numbered 1 and 2; `2 players match the current command: filter t/Striker t/Captain t/striker`. |
| `filter t/strik`, then `filter t/NoSuchTag`. | Each is a successful empty list with `0 players match the current command: <command>`, echoing the respective filter. |
| `find Amy`, then `filter t/Goalkeeper`. | Bob appears despite being hidden by the name search. |
| `filter t/Captain`, then `find Bob`. | Bob appears; the tag predicate is replaced. |
| `squad`, then `filter t/Striker`. | Amy appears although not kept; the squad predicate is replaced. |
| `squad`, then `find Amy`. | Amy appears; name search also replaces the squad predicate. |
| `filter t/Captain`, then `squad`. | Bob and Charlie appear; Amy is hidden because she is not kept. |
| `filter i/INJURED`, then `filter t/Captain i/fit`. | First Amy only, then Charlie only. Tags use OR with each other and AND with injury status. |
| Try `filter t/`, `filter`, `filter Striker`, `filter t/Bad!`, `filter i/hurt`, `filter i/fit i/injured`, and `Filter t/Striker`. | Each reports the appropriate tag, format, status, duplicate-prefix or unknown-command error; Charlie remains the previous result and the file is not written. |
| `filter t/123456789012345678901234567890`, then the same label with one extra `1`. | The 30-character label searches successfully with no matches; 31 characters is rejected without changing that view. |
| `list`, `sort rating`, then `filter t/Captain`. | Charlie is 1 and Amy is 2: the active descending rating sort is retained, rather than roster order. |
| `tag 1 t/Goalkeeper` after the previous row. | Charlie gains Goalkeeper and keeps Captain; he remains 1 and Amy remains 2 in the sorted Captain results. |
| `find Charlie`, then `filter t/Goalkeeper`. | Bob and Charlie are both found; Charlie precedes unrated Bob under the active rating sort. |
| `filter t/Goalkeeper`, then `delete 2`. | Unrated Bob is deleted using the displayed index; Charlie remains as 1 and Amy stays hidden. |
| `add n/Dana p/444 t/Captain` | Full roster returns with the active sort: Charlie, Amy, Dana. The add clears the filter but retains sorting. |
| `list` | Amy, Charlie, Dana in insertion order, numbered 1–3; sorting is cleared. |
| `filter t/Captain`, then `sort rating all` | All three players return, in order Charlie, Amy, Dana; the explicit all scope replaces the tag filter. |
| `sort rating squad`, then `filter t/Captain`, then `list` | First Charlie only (the kept squad); next Charlie, Amy and Dana (the tag filter replaces squad scope while retaining the active rating sort); finally all three in insertion order with sorting cleared. |
| `help`, then `filter t/Goalkeeper`. | Player panel returns showing Charlie; help includes current filter syntax. |
| Exit and relaunch from the same folder, then `filter t/Goalkeeper`. | Full insertion-order roster appears on launch; the subsequent filter finds Charlie with persisted Goalkeeper and rating 9. |
| `clear`, then `filter t/Striker`, then `find Amy`. | Both searches give `0 players match the current command: <command>`, echoing the respective command, without exceptions on an empty roster. |

Compare the JSON contents and modification time before and after valid, empty-result and invalid filters: neither changes. Block the save path using the procedure under [Finding players by name](#finding-players-by-name-f3-us04); filtering must still show ordinary results without a save error. Restore the data directory before modifying players or closing the test session. Close the app and remove only the disposable test data after completion.
