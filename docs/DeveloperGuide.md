---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

## **Value proposition**

Sports coaches find it troublesome to **keep track** of their players and their specific details, especially when they are ranking or cutting the team. Our product will provide a clear overview of all players, with fast lookup, custom categorisations, amend and remark functionality, and a system to evaluate player performance.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

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
an I/O failure after mutation leaves the change in memory and produces the
save-failure `CommandException`. The UI observes changes to the displayed list,
rather than needing a saved-file reload after each command.

Each component defines an interface and a manager implementation. The following
partial diagram retains the shared contracts without expanding feature internals:

<puml src="diagrams/ComponentManagers.puml" width="420" alt="LogicManager depends on Model and Storage interfaces" />

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

See the [player-card detail](diagrams/PlayerCardClassDiagram.html) for the planned
US21 remarks and US13 injury-status display.

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API:** [`Logic.java`](https://github.com/AY2627S1-CS2103T-W13-1/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

<puml src="diagrams/LogicClassDiagram.puml" width="720" alt="LogicManager coordinates parsing, command execution, Model and Storage" />

`LogicManager#execute(commandText)` performs the following steps:

1. `AddressBookParser#parseCommand` identifies the lowercase command word. Commands needing arguments delegate to their command parser; parameterless commands such as `list`, `squad`, `help` and `exit` are constructed directly. `clear` also checks that its arguments are blank.
2. The parser returns a concrete `Command`. Shared `ParserUtil` operations validate values and map one-based input to an `Index`; they do not resolve which player occupies that index.
3. An index-based `Command#execute(model)` resolves the index against `Model#getFilteredPersonList()`, which is the **displayed sorted view**, despite the historical method name. A mutation replaces a `Person` or changes the roster; a view command changes a predicate/comparator.
4. `LogicManager` skips `Storage#saveAddressBook` for `FindCommand`, `FilterCommand`, `SortCommand`, `ListCommand`, `SquadCommand`, `HelpCommand` and `ExitCommand`. Other successful command types save `model.getAddressBook()`, even if their feedback reports no data change.
5. A successful `CommandResult` contains feedback and flags for showing help or exiting. An `IOException` while saving is translated into the existing save-failure `CommandException`; the model is not rolled back.

The existing command-level sequence remains a useful example:

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions inside Logic for delete 1" />

<box type="info" seamless>

The `DeleteCommandParser` lifeline should end at its destroy marker (X); PlantUML continues the lifeline visually.
</box>

The parser helper diagram is partial: `XYZCommandParser` denotes a family of real
classes, including `RateCommandParser`, `SortCommandParser`, `SetStatusCommandParser`,
`RemarkCommandParser` and `InjuryCommandParser`. `keep`, `cut` and `pending` share
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

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<pic src="images/StorageClassDiagram.png" width="800" alt="Class diagram of the Storage component" />

The image is drawn from [`StorageClassDiagram.svg`](diagrams/StorageClassDiagram.svg). After editing the SVG, regenerate the PNG at twice its size, e.g. with Chrome:
`chrome --headless --force-device-scale-factor=2 --window-size=960,600 --screenshot=docs/images/StorageClassDiagram.png docs/diagrams/StorageClassDiagram.svg`.

The `Storage` component,
* can save both the team's player data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* saves each player as a `JsonAdaptedPerson`, which holds every field as a `String` and checks each one when converting it back into a `Person`.
* reads a field that is missing from the file as absent, so data files saved before a field existed still load. For example, a player from a v1.3 file has no jersey number.
* writes the jersey number only for a player who has one; for a player without one, the field is left out of the file.
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Adding players, jersey numbers and duplicate warnings

#### Implementation

`add` creates a new `Person` from a name, a phone number, an optional jersey number and optional tags. The sequence diagram below shows how `add n/Amy Tan p/91234567 j/7` is executed.

<puml src="diagrams/AddSequenceDiagram.puml" alt="Interactions inside the Logic, Model and Storage components for an add command with a jersey number" />

<box type="info" seamless>

**Note:** The lifeline for `AddCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

1. `AddCommandParser` tokenizes the arguments. It also looks for `r/` and `i/`, but only so that `ParserUtil#requireNoRemarkOrInjury()` can reject them with a message that names the right command. Without this, `r/...` would be read as part of the value before it, and the error would blame that value instead. `n/` and `p/` are compulsory, and `ArgumentMultimap#verifyNoDuplicatePrefixesFor()` rejects a repeated `n/`, `p/` or `j/`.
1. `ParserUtil` parses each value. `parseName()` collapses repeated spaces with `Name#normalize()` before checking the allowed characters and the 100-character limit separately, so that each problem has its own message. `parsePhone()` removes spaces, hyphens, dots and brackets with `Phone#normalize()` before checking for 3 to 15 digits. `parseJerseyNumber()` accepts one or two digits, and `JerseyNumber` stores the value as an `int`, so `07` and `7` are the same number.
1. The parser creates the player with `new Person(name, phone, tags)`, which starts as `PENDING`, `UNRATED`, `FIT` and with an empty remark, and sets the jersey number with `Person#withJerseyNumber()`. A `Person` stores a missing jersey number as `null` but exposes it only as an `Optional<JerseyNumber>` from `getJerseyNumber()`.
1. `AddCommand#execute()` rejects the player if `Model#hasPerson()` finds a player with the same name. This uses `Person#isSamePerson()`, which compares names with `Name#isSameName()`, ignoring case and repeated spaces. Otherwise, `Model#addPerson()` adds the player and resets the filtered list to show the whole team, so the coach sees the new player.
1. `AddCommand` then asks two package-private helpers in the `logic.commands` package for warnings. `DuplicateJerseyNumberChecker#getWarning()` looks for other players with the same jersey number (`Person#hasSameJerseyNumber()`), and `SimilarNameChecker#getWarning()` looks for names that are equal once spaces and punctuation are ignored (`Name#isSimilarTo()`). Both search `Model#getAddressBook()`, i.e. the whole team rather than the filtered list, so they also find players hidden by `find` or `filter`. Each returns an empty string or a warning line, which is appended to the success message.
1. `LogicManager` saves the team through `Storage`. `JsonAdaptedPerson` writes `jerseyNumber` only for a player who has one, and reads a missing field as no jersey number.

`edit` reuses the same parsing and checks, with these differences:

* `EditPersonDescriptor` records whether `j/` was given in `isJerseyNumberEdited`, as a `null` jersey number can mean either "not given" or "remove it". `EditCommandParser#parseJerseyNumberForEdit()` turns an empty `j/` into `null`, which removes the jersey number.
* The duplicate-name check runs only when the new name belongs to another player (`!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)`), so a coach can change the capitalisation or spacing of a player's own name.
* `EditCommand` builds the edited player from the old one, so the selection status, rating, remark and injury status are always kept, and so are the tags and jersey number unless `t/` or `j/` is given. If the result equals the old player, the command reports that nothing changed instead of replacing it.
* The jersey number warning is checked only when the edit includes `j/`, and the similar-name warning only when the name actually changes. This stops edits to other details from repeating a warning the coach has already seen.
* `Model#setPerson()` replaces the player without resetting the filtered list, so the coach can keep editing search results by the same indices.

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
  * Cons: Each check scans the whole team once per command. This is fast enough for the 1000-player limit in the non-functional requirements.
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

### Rating sorting (US17) and list reset (US02)

`SortCommandParser` accepts `rating` and an optional `asc`/`desc`, case-insensitively;
omitting the direction means descending. It does not accept a roster/squad scope
argument in this version. `SortCommand` changes only `Model#setPersonComparator`,
so it sorts the existing name/tag/injury/squad view without selecting hidden players.
`find`, `filter` and `squad` replace the predicate but retain the comparator. `add`
restores the all-player predicate while retaining the comparator; `edit` and `rate`
retain both, with automatic removal/repositioning when an update affects the view.

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
1. As `Person` is immutable, `p.withSelectionStatus(CUT)` returns a new `Person` with every other field (name, phone, rating, tags and remark) unchanged. The command replaces `p` with it using `Model#setPerson()`.
1. `LogicManager` then saves the team through `Storage`. It decides this by command type: read-only command types, such as `find` and `squad`, skip this step, while `keep`, `cut` and `pending` always save.

If the index is out of range, the command throws a `CommandException` before changing anything. If the player already has the status, the command returns a message saying so and does not replace the player. `LogicManager` still saves in this case, as it only checks the command type, but the saved data is unchanged.

The status is saved in the data file as a `selectionStatus` field. Data files saved by v1.3 have no such field, so `JsonAdaptedPerson` loads those players as `PENDING`.

#### Design considerations

**Aspect: How to represent the three commands**

* **Alternative 1 (current choice):** One `SetStatusCommand` that takes the status to set.
  * Pros: No repeated code. Adding a new status needs a new enum value and command word, a case in `SetStatusCommand#getCommandWord()`, and a case in `AddressBookParser`.
  * Cons: The shared class needs a mapping between each status and its command word, which the separate classes would not need.
* **Alternative 2:** Separate `KeepCommand`, `CutCommand` and `PendingCommand` classes.
  * Pros: Each class matches one command word, like the rest of AB3.
  * Cons: Three almost identical classes and parsers to keep in sync.

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
`Tag` validates and normalizes labels and compares them ignoring case.

`AddCommandParser` collects all `t/` values through `ParserUtil.parseTags`.
`EditCommandParser` distinguishes an omitted tag prefix (preserve tags), supplied
labels (replace the set), and a single empty prefix (clear the set). Empty labels
mixed with other labels are invalid. The edit descriptor and player defensively
copy their sets. `JsonAdaptedPerson` writes tag strings and accepts missing/null
`tags` from older records as an empty collection; invalid/null entries are rejected.
`PersonCard` displays tags alphabetically in a wrapping `FlowPane`.

### Filtering players by tag (US16)

`FilterCommandParser` accepts repeated `t/` arguments and reuses `ParserUtil.parseTags`.
`TagsContainAnyPredicate` holds a defensive copy of the requested tags and matches
any complete label using `Tag` equality. `FilterCommand` replaces the model's list
predicate, so each filter searches the full roster. `LogicManager` treats it as read-only
and skips saving, including when the data file is unreadable or unwritable.

<puml src="diagrams/TagFilterClassDiagram.puml" width="650" />

The UML shows creation dependencies from the parser and the predicate's realization
of `Predicate<Person>`. The predicate reads `Person` tags; the command changes only
the filtered view through `Model`. Matches retain roster order, use displayed indices,
and are replaced by `find` or `list`. Invalid arguments fail before the view changes.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


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

Sports coaches find it troublesome to **keep track** of their players and their specific details, especially when they are ranking or cutting the team. Our product will provide a clear overview of all players, with fast lookup, custom categorisations, amend and remark functionality, and a system to evaluate player performance.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

These 10 stories consolidate the key needs in the team's project notes and describe the intended product, including features beyond the MVP.

| Priority | As a … | I want to … | So that I can … |
|----------|--------|-------------|----------------|
| `* * *` | coach | record each player's name, contact number, and jersey number | keep an identifiable record of each player and match them to my trial notes and videos |
| `* * *` | coach | list all players | see everyone in the team at a glance |
| `* * *` | coach | edit a player's details or delete their record | keep my records accurate when details change, mistakes are found, or players withdraw |
| `* * *` | coach | find players by name | retrieve their details during a session without scanning the entire list |
| `* * *` | coach | view a summary of command formats within the app | recall the syntax I need without leaving the app |
| `* * *` | coach | have changes to player data saved automatically | resume work in a later session without re-entering changes or remembering to save manually |
| `* * *` | coach | organise players using custom tags and filter the list by tag | focus on players in a particular position, role, or trial group |
| `* * *` | coach | mark players as kept or cut and view the kept players | track my selection decisions and see the resulting squad |
| `* *` | coach | set or remove player ratings, distinguish Unrated from 0, and sort the displayed players by rating | compare assessed players on a common scale while identifying those who still need evaluation |
| `*` | coach | add free-text remarks to a player's record | retain qualitative observations that a numeric rating cannot express when making selection decisions |

### Use cases

For all use cases below, the **System** is **CouchCoach** and the
**Actor** is the **Coach**, unless specified otherwise.

These use cases describe the intended product behaviour. UC01 is part of
the MVP. UC02–UC06 describe planned features beyond the MVP and do not
indicate that those features have already been implemented.

The following rules apply throughout:

* A player selected from a displayed list refers to a player in that list,
  which may contain all players or only search results. For deletion,
  selection uses the player's current index in the displayed list.
* Duplicate-name detection is part of the MVP. Names are compared after
  trimming leading and trailing spaces, collapsing repeated internal spaces,
  and ignoring letter case. Players may share a contact number.
* The proposed edit behaviour preserves the same name-uniqueness rule:
  changing a player's name must not duplicate another player's name.
* Rejected requests leave player data unchanged and do not trigger a save.
* Successful data changes are saved automatically. The planned modifying
  features below follow the same saving behaviour as MVP deletion. If saving
  fails, the change remains applied in the current session, and CouchCoach
  warns that the change has not been saved and may be lost after closing.

#### UC01: Delete a player

**Related user stories:** US02, US03, US04

**MSS**

1. Coach requests to list all players.
2. CouchCoach displays the player list.
3. Coach requests to delete a player using their index in the displayed list.
4. CouchCoach removes the player, saves the updated data, and displays the
   deleted player's details and updated list. The remaining displayed players
   are renumbered.

   Use case ends.

**Extensions**

* 1a. Coach requests to find players by name instead of listing all players.

    * 1a1. CouchCoach processes the search request.

      Use case resumes at step 2, displaying only the matching players.
      The search filter remains active after deletion.

    * 1a1a. The search request has no keywords.

        * 1a1a1. CouchCoach displays an error and leaves the displayed list unchanged.

          Use case resumes at step 1.

* 2a. The displayed list is empty.

  Use case ends.

* 3a. The deletion request is malformed or the index does not identify a
  player in the displayed list.

    * 3a1. CouchCoach displays an error and leaves the data unchanged.

      Use case resumes at step 3.

* 4a. CouchCoach cannot save the updated data.

    * 4a1. CouchCoach informs the Coach that the deletion was applied in the
      current session but could not be saved and may be lost after closing.

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
   labels to assign to that player.
4. CouchCoach applies the requested labels, saves the updated data, and
   displays the player's updated categories.

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

* 4a. CouchCoach cannot save the updated data.

    * 4a1. CouchCoach informs the Coach that the category changes were applied
      in the current session but could not be saved and may be lost after closing.

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

**Related user story:** US10

**MSS**

1. Coach requests to list all players.
2. CouchCoach displays the player list for the Coach to review.
3. Coach identifies a player from the displayed list and requests to mark
   that player as kept or cut.
4. CouchCoach records the selection decision, saves the updated data, and
   displays the player's updated selection status.

   Use case ends.

**Extensions**

* 2a. The player list is empty.

  Use case ends.

* 3a. The request does not identify a player in the displayed list.

    * 3a1. CouchCoach displays an error and leaves the data unchanged.

      Use case resumes at step 3.

* 3b. The selection status is missing or is neither kept nor cut.

    * 3b1. CouchCoach explains the permitted selection statuses and leaves the
      data unchanged.

      Use case resumes at step 3.

* 4a. CouchCoach cannot save the updated data.

    * 4a1. CouchCoach informs the Coach that the selection decision was recorded
      in the current session but could not be saved and may be lost after closing.

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

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should respond to any command within 2 seconds when storing up to 1000 players, on a computer that meets the minimum requirements of its mainstream OS.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Should be usable by a single user on one computer; concurrent use of the same data by multiple users is not required.
5.  Should work without an internet connection, and should not send player data to any remote server.
6.  Should store player data locally in a _human-editable_ text file, without using a database management system.
7.  Should save each successful change to player data to the data file before the result of that command is displayed, so that no confirmed change is lost if the app is closed afterwards.
8.  Should be distributed as a single JAR file of at most 100MB that runs without an installer.
9.  The GUI should display all content without clipping for standard screen resolutions of 1920x1080 and higher at screen scales of 100% and 125%, and all functions should remain usable for resolutions of 1280x720 and higher at a screen scale of 150%.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **CLI (Command Line Interface)**: A text-based interface in which the user types commands to interact with the app
* **GUI (Graphical User Interface)**: The visual interface of the app, which displays the player list and command results
* **Coach**: The user of CouchCoach, who manages the players of a sports team
* **Player**: A member or prospective member of the Coach's team whose details are recorded in CouchCoach
* **Displayed list**: The list of players currently shown in the GUI, which contains either all players or only the players matching the most recent search or filter
* **Index**: The position number of a player in the displayed list, starting from 1, used to identify that player in a command
* **Duplicate name**: A player name that matches an existing player's name after trimming leading and trailing spaces, collapsing repeated internal spaces, and ignoring letter case
* **Tag**: A custom label that the Coach assigns to a player to categorise them, such as by position, role, or trial group
* **Rating**: A numeric score that the Coach assigns to a player to compare assessed players on a common scale; a player without a rating is _unrated_
* **Selection status**: The Coach's selection decision for a player, which is either _kept_ (selected for the team) or _cut_ (not selected)
* **Remark**: A free-text note that the Coach records about a player to retain qualitative observations
* **Human-editable**: Stored in a plain-text format that can be read and modified using a common text editor

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
and record the commit/JAR identity. These instructions describe the merged command
syntax `sort rating [asc|desc]`; do not assume a scope argument exists.

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
| `find Ten`, `sort rating`, `find Unrated` | First Bob only; the later search replaces the old filter and shows Dee then Eli. |
| `squad`, `sort rating` | Bob, Cy, Amy only. Sorting does not add pending players. |
| `sort rating`, `find Unrated`, `squad` | Squad replaces the search and restores all kept players in active rating order: Bob, Cy, Amy. |
| `sort rating`, `edit 1 p/999` | Bob's phone changes; order and scores stay Bob, Cy, Amy, Dee, Eli. |
| `sort rating`, `rate 1 0` | Bob is the target. New order: Cy, Amy, Bob, Dee, Eli; Amy precedes Bob in the zero-score tie. |
| `sort rating`, `rate 1 unrated` | Bob becomes Unrated. New order: Cy, Amy, Bob, Dee, Eli; Bob precedes the later-added Unrated players. |
| `sort rating`, `delete 1`, `list` | Bob is deleted; list shows Amy, Cy, Dee, Eli in insertion order. |
| `filter t/Trial`, `sort rating`, `add n/Fay New p/678` | Full roster returns, retaining the sort: Bob, Cy, Amy, Dee, Eli, Fay. Fay is Unrated. |
| `filter t/Trial`, `sort rating`, `edit 1 n/Bob Changed` | Bob's details change, current tag filter and sort stay active: Bob, Amy, Dee. |
| `sort rating`, then `list all` | Full insertion order Amy, Bob, Cy, Dee, Eli; `Listed all players`. Sorting is cleared. |
| `sort`, `sort name`, `sort rating up`, `sort rating asc extra` | Format error with sort usage. Previous displayed view and order unchanged. |
| `clear`, `sort rating`, `sort rating asc`, `list` | Empty view; both sorts succeed and list reports `Listed all players`. |
| `sort rating`, then restart from the same directory | Amy, Bob, Cy, Dee, Eli in insertion order; ratings 0, 10, 10, Unrated, Unrated persist. The previous filter/sort does not. |

For a sort/list-only row, record the file's bytes and modification time after setup;
neither should change. Repeat `filter t/Trial`, `sort rating`, `list` with storage
blocked by renaming `data` and placing a regular file named `data` at that path.
All three view commands should still succeed. Remove that blocker and restore the
directory before a mutation or restart. To check rate's save failure, use the same
blocker and run `rate 1 8`: Amy changes on screen, but the save-failure feedback
appears. Restore storage and make a successful data-changing request before testing
restart. Confirm both Amy's new rating and all preserved fields in the reloaded file.

**Cleanup:** close the app, restore any renamed directory, and remove only the
disposable test directory. Keep any recorded evidence outside the real team's data.
Do not run `clear` against a coach's working roster.

### Finding players by name (F3 / US04)

Run these checks in a separate folder using disposable data. Prepare the team in this order:

```text
clear
add n/Tan Wei Ming p/91234567
add n/Lim Wei Jie p/92345678
add n/Nur Aisyah p/93456789
add n/Muthu s/o Ramasamy p/94567890
```

The name character set permits the slash-containing record needed by F3,
as well as apostrophes, hyphens, and periods.

| Test case | Expected result |
| --- | --- |
| `find wei` | Tan and Lim displayed in that order, numbered 1 and 2; `2 players listed!`. |
| `find TAN` | Tan only, numbered 1; `1 player listed!`. |
| `find wei nur` | Tan, Lim, and Nur once each; `3 players listed!`. |
| `find ming tan ming` | Tan once; `1 player listed!`. |
| `find We` | Empty panel; `0 players listed!` (no partial matching). |
| `find 91234567` | Empty panel; phone numbers are not searched. |
| `find S/O` | Muthu only; `1 player listed!`. |
| `find s` | Muthu only; `1 player listed!` (a part of `s/o` between punctuation matches). |
| `find n/Wei` | Empty panel; the slash keyword is literal, not a name prefix. |
| `find` or `find` followed only by spaces | Exact four-line format error documented in the UG; previous displayed list unchanged. |
| `Find wei` | Unknown-command error; previous displayed list unchanged. |
| `find   wei   ming` | Same matches as the equivalent command with single spaces. |
| `find tan` followed by `find nur` | Nur appears despite being hidden by the first search. |
| `list` after a search | All four players displayed in original order. |
| `find wei`, then `delete 1` | Tan removed; Lim remains numbered 1; Nur and Muthu stay hidden. |
| `delete 1` again | Lim removed; filtered panel empty even though Nur and Muthu remain in the team. |
| `list`, `clear`, then `find wei` | Empty panel; `0 players listed!`; no failure on an empty team. |

Before running successful and invalid searches, record the data file's contents and
modification time. Both must remain unchanged after every search. To check searches
with unavailable storage, rename the data directory after the team is loaded and
create a regular file named `data` in its place, blocking directory creation. Execute `find wei` and verify that
the normal search feedback still appears. Remove the temporary file and restore the
directory before adding or deleting players. Relaunch after preparing Muthu's record
and confirm that `find s/o` still finds it, checking that the slash survives storage.

Implementation: `FindCommandParser` splits plain keywords on whitespace;
`NameContainsKeywordsPredicate` uses case-insensitive whole-word OR matching over
names. A keyword also matches a part of a word between punctuation, so `Ming` matches
`Tan Wei-Ming`. Updating the model's filtered list replaces the previous predicate, so each
search considers the full team. The existing JavaFX list cells supply displayed
indices. `LogicManager` returns a successful find result without saving player data.

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Viewing help

Prerequisites: Launch CouchCoach with at least one player and run `list`. Record the
players' names, phone numbers, and order before these checks. Run `list` before each
independent help entry-point check so that the player list is visible first.

| Test case | Expected result |
| --- | --- |
| Enter `help`. | The player-list area shows the **CouchCoach commands:** summary in the main window, with usage for `add`, `list`, `find`, `delete`, `help`, and `exit`. The result box shows `Showing CouchCoach command summary.` No separate window opens. |
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

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_

### Custom player tags (US08): manual regression checks

Use a disposable data folder. Start with `add n/Tag Tester p/123 t/Striker t/Trial Group 2`.

| Action | Expected result |
| --- | --- |
| Inspect the new card and result box. | Both tags appear, alphabetically, without hiding name or phone. |
| `edit 1 p/456` | Tags remain. |
| `edit 1 t/Captain t/captain` | Previous tags are replaced by one Captain tag. |
| `edit 1 t/` | Tags are cleared; name and phone remain. |
| `edit 1 t/` again | Successful idempotent clearing. |
| `edit 1 t/` followed by `edit 1 t/Trial   Group 2` | One tag, Trial Group 2, with collapsed whitespace. |
| `add n/Invalid p/456 t/`, `edit 1 t/Bad!`, `edit 1 t/ t/Striker` | Tag constraint error; records and displayed list unchanged. |
| Try 30-character and 31-character tags. | The first succeeds; the second is rejected with the tag constraint error. |
| `find Tag`, then `edit 1 t/Striker` | The displayed player is edited; the full team is restored. |
| Restart, then inspect cards and JSON. | Tags persist. A legacy record with no tags field still loads. |
| Open `help` at normal and minimum window width. | Add/edit syntax, examples, and tag-clearing guidance remain readable. |
| `edit 1 t/Striker`, `keep 1`, then `rate 1 10` | The same player shows Striker, Kept, and Rating: 10/10 together. |
| `squad`, then `rate 1 0` | The kept player remains visible with Striker and Rating: 0/10. |
| `edit 1 t/Captain`, then `edit 1 p/456` | Kept and Rating: 0/10 remain; the Captain tag survives the phone edit. |
| `edit 1 t/`, then restart | Only tags are cleared; the player remains kept and rated 0/10. |
| `cut 1`, then `squad`; use `list` to inspect the player again | The cut player leaves the squad; their rating and other details remain. |

Run the existing add, edit, find, delete and saving regressions as well. Back up data
before checking corrupt JSON tags; invalid tag strings and null tag entries should
trigger the existing unreadable-data warning rather than silently creating labels.

### Filtering by tag (US16): manual regression checks

In a disposable team, add Amy with Striker and Captain, Bob with Goalkeeper, and
Charlie with Captain, in that order. Confirm tags are visible on their cards.

| Action | Expected result |
| --- | --- |
| `filter t/STRIKER` | Amy only; 1 player listed. |
| `keep 1`, `rate 1 10`, `squad`, then `filter t/STRIKER` | Amy remains visible with Striker, Captain, Kept, and Rating: 10/10. |
| `filter t/Striker t/Captain t/striker` | Amy and Charlie once each, numbered 1 and 2. |
| `filter t/strik` or `filter t/NoSuchTag` | Empty list; 0 players listed. |
| `filter t/Goalkeeper` after `find Amy` | Bob appears despite the earlier name filter. |
| `filter t/Captain`, then `find Bob` | Bob appears; find replaces the tag filter. |
| `filter t/`, `filter`, `filter Striker`, `filter t/Bad!`, `Filter t/Striker` | Error; previous list and player data unchanged. |
| Use tags of 30 and 31 characters. | A valid 30-character label is searched; 31 is rejected. |
| `filter t/Captain`, then `delete 1` | Amy is deleted; Charlie remains numbered 1, Bob stays hidden. |
| `edit 1 t/Striker` after the previous step | Charlie's tags change; full roster returns. |
| `filter t/Goalkeeper`, then add a new player. | Full roster returns, with the new player last. |
| `help`, then `filter t/Goalkeeper` | Player panel returns showing Bob. Help includes filter syntax and examples. |
| `list` after any filter | Full roster restored in its original order. |
| Clear the disposable team, then `filter t/Striker`. | 0 players listed; no exception. |

Compare the JSON file before and after successful/failed filtering; its contents must
not change. Block the save path as in the existing find tests: filtering still works
without a save error. Add/edit/delete remain subject to their existing save behavior.
