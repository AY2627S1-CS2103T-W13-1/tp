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

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

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

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a player from the team).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

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
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

Each `Person` (a player) holds one `Name`, one `Phone`, one `SelectionStatus`, one
`Rating`, one `Remark`, one `InjuryStatus`, zero or one `JerseyNumber`, and zero or more
`Tag` values. Empty remarks
and unrated ratings are present value objects, so their multiplicity is still `1`.
These are ordinary associations: immutable
values can be shared between player versions, so deleting one player does not imply
deleting its value objects. `AddressBook` exclusively contains its `UniquePersonList`,
which is shown using composition. The full roster and filtered view reference the
same player objects; filtering does not create copies.

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

The assessment view reflects the merged selection, rating, remarks, and injury features.
Jersey numbers are now an optional field of `Person`, but these diagrams do not show
them yet; reconcile that field in the model diagrams. The [rating and sorting UML](diagrams/player-rating-sorting-uml.html)
provides the separate command-level design view.


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

`JsonUtil.readJsonFile` rejects a null document with `DataLoadingException`.
`JsonUserPrefsStorage` also rejects null GUI settings before the model copies preferences.
These checks use the existing startup fallbacks rather than catching unrelated runtime errors.

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

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
* `EditCommand` builds the edited player from the old one, so the selection status, rating, remark and injury status are always kept, and so are the tags. The jersey number is kept unless `j/` is given. `TagCommand` reuses the edit descriptor internally to replace tags. If the result equals the old player, the command reports that nothing changed instead of replacing it.
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

`RateCommandParser` parses `rate INDEX RATING` into a `RateCommand`. It validates the
positive displayed index and a whole-number rating from 0 to 10. The word `unrated`, in
any letter case, is parsed as `Rating.UNRATED` instead, so the same command removes a
rating. The command checks that the index exists in the displayed list before changing
the model. Removing the rating of a player who is already unrated changes nothing.

The sequence below follows successful execution of `rate 1 8`:

<puml src="diagrams/RateSequenceDiagram.puml" alt="Sequence of parsing, player replacement and saving for the rate 1 8 command" />

`RateCommand` replaces the selected immutable `Person` with a copy containing the new
rating. Name, phone, selection status, tags, remark, injury status and jersey number
are preserved. The current filter stays active; an active rating sort may move the
player and change displayed indices.

After command execution, `LogicManager` saves the team through `Storage` before
returning the result. An invalid displayed index raises `CommandException` before
replacement. If saving fails, the update remains in memory and the coach receives
the save-failure message.

The diagram summarizes parser internals and object creation. Its editable source is
[RateSequenceDiagram.puml](diagrams/RateSequenceDiagram.puml). The
[default-style HTML view](diagrams/RateSequenceDiagram.html) is generated from that
source with `python3 docs/scripts/generate_rate_sequence.py`; use `--check` to verify
that the HTML is up to date. Presentation settings are kept in the HTML template.

### Rating sort scopes

`SortCommandParser` accepts `sort rating [all|squad] [asc|desc]`, ignoring letter case
for arguments, with scope and direction accepted in either order. A missing scope maps to `Scope.CURRENT`, preserving the current predicate.
`Scope.ALL` selects all players; `Scope.SQUAD` installs the shared `HasSelectionStatusPredicate.IS_KEPT` predicate.
Explicit scope selection replaces rather than intersects a previous name/tag/injury filter.

`Direction.ASCENDING` and `Direction.DESCENDING` express the requested order.
`SortCommand` applies its comparator to the model's displayed list. Rated players are ordered
numerically and unrated players stay last in either direction. `ModelManager` breaks ties by
source insertion order. The roster and saved file are unchanged, and later view commands may
replace the chosen scope while retaining the comparator. `list` clears both filter and comparator.
The existing no-scope syntax remains compatible with workflows that sort a search result.

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
`Tag` trims surrounding whitespace, collapses internal whitespace, validates the allowed characters and the 30-code-point length limit, and preserves display spelling. Equality and hashing use the same case-folded label, so case variants cannot become separate tags. Tags do not participate in player identity.

`AddCommandParser` collects initial `t/` values through `ParserUtil.parseTags`. Its `LinkedHashSet` keeps the first spelling among case-insensitive duplicates. `TagCommandParser` parses `tag INDEX t/TAG [t/TAG]...`: supplied labels replace the entire set, and a single empty prefix clears it. Missing prefixes and empty labels mixed with other labels fail before any field changes. `EditCommandParser` rejects tag prefixes with migration guidance before changing any fields; its public syntax accepts only name, phone and jersey edits. Replacement makes the requested final categories explicit; it does not append labels or remove one label by name.

`TagCommand` delegates player replacement to an `EditCommand` containing only a tag replacement descriptor, sharing field preservation and filter handling. `TagCommand.execute` and `EditCommand.execute` resolve the index against `Model#getFilteredPersonList`, which exposes the displayed, possibly sorted view. It creates a replacement `Person`, preserving fields omitted from the descriptor, including rating, selection status, remark, injury status and jersey number. The descriptor and player defensively copy tag sets so later caller changes cannot mutate a stored player. `Model#setPerson` replaces the roster entry without resetting the predicate or comparator. A player whose new tags no longer satisfy the active filter disappears; the remaining displayed indices change accordingly.

The edit no-change check compares tag spellings as well as `Person` equality: case-only spelling changes must still be applied. `TagCommand` formats successful replacements as `Updated tags for NAME: LABELS` and removals as `NAME: tags cleared.`, retaining the shared `No changes made` result for an identical replacement. `LogicManager` saves after a successfully executed tag command, including a no-change result; parse and execution failures do not save. A save failure leaves the in-memory change applied and reports the save warning instead of ordinary edit feedback.

`JsonAdaptedPerson` writes tag strings in case-insensitive alphabetical order and accepts missing/null `tags` from older records as an empty collection; invalid/null entries are rejected. `PersonCard` displays tags alphabetically in a wrapping `FlowPane`. Keeping tags as immutable values shared by parser, model and storage gives updates and filtering the same label rules without coupling identity to a coach's changing categories.

### Filtering players by tag (US16)

`FilterCommandParser` accepts repeated `t/` arguments and reuses `ParserUtil.parseTags`. It also accepts one optional `i/` injury status and requires at least one condition. `TagsContainAnyPredicate` holds a defensive copy of the requested tags and matches any complete label using `Tag` equality (OR). `FilterCommand#matches` combines that result with the optional injury status using AND. An injury-only filter needs no tag predicate. Invalid labels, an invalid/repeated injury status or unexpected preamble fail before the view changes.

`FilterCommand` replaces the model's list predicate, so each filter searches the full roster rather than intersecting with an earlier name search, filter or squad view. `LogicManager` treats filtering as read-only and skips saving, including when the data file is unreadable or unwritable.

<puml src="diagrams/TagFilterClassDiagram.puml" width="650" />

This partial UML shows the tag-matching path; the optional injury-status condition is omitted. The parser creates the command and optional tag predicate. The predicate realizes `Predicate<Person>` and reads player tags; the command changes the filtered view through `Model`.

`ModelManager` wraps the roster in a `FilteredList`, then a `SortedList`, and exposes an unmodifiable observable view. Changing the predicate retains the active rating comparator. Results therefore use rating order when sorting is active, otherwise insertion order, and are numbered from 1. `find`, `filter` and `squad` replace one another's predicate while retaining sorting; `list` clears both predicate and comparator. This separates a coach's view from persisted roster order and avoids changing records just to browse a group.

### Name search implementation (F3 / US04)

`AddressBookParser` dispatches `find` to `FindCommandParser`, which rejects an empty argument string and splits plain keywords on whitespace. `NameContainsKeywordsPredicate` accepts a player if any keyword matches a whole whitespace-delimited word of the original name or a complete part separated by punctuation. It uses `StringUtil.containsWordIgnoreCase` for both representations. Thus both `Wei-Ming` and `Ming` match `Tan Wei-Ming`, while `Min` does not. Letters, combining marks and numbers stay together when punctuation is replaced by spaces; slashes in keywords remain literal, so `s/o` can match the original name.

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
| US02 | `* * *` | coach | list all players | see everyone in the team at a glance | `list` (F2) |
| US03 | `* * *` | coach | delete a player | remove withdrawn or mistaken entries | `delete` (F4) |
| US04 | `* * *` | coach | find players by name | retrieve details without scanning the entire roster | Whole-word and punctuation-part `find` (F3) |
| US05 | `* * *` | coach | view command formats inside the app | recall syntax without leaving the app | `help` (F5) |
| US06 | `* * *` | coach | have player changes saved automatically | resume work without re-entering changes | Automatic local saving (F7), with a warning on failure |
| US07 | `* * *` | coach | edit a player's details | correct mistakes without re-entering the record | `edit` |
| US08 | `* * *` | coach | assign my own labels to players | organise positions, roles, seasons and trial groups | Tags through `add` and `tag` |
| US09 | `* * *` | coach | record a player's jersey number | identify them from trial notes and videos | Optional jersey number through `add` and `edit` |
| US10 | `* * *` | coach | mark players as kept, cut or pending | track and revise selection decisions | `keep`, `cut`, `pending` |
| US11 | `* *` | coach | be warned about duplicate names | avoid duplicate player records | Exact duplicate rejection and similar-name warnings |
| US12 | `* *` | coach | give players numeric ratings | compare assessed players on a common scale | `rate`, including removal with `unrated` |
| US13 | `* *` | coach | mark a player as injured | distinguish injury from a selection decision | `injury` and injury filtering |
| US14 | `* *` | coach | see which players remain unrated | notice players who still need evaluation | Unrated cards and placement after rated players when sorting; no dedicated unrated filter |
| US16 | `* *` | coach | filter players by tag | focus on one group | `filter`, with OR across tags and optional injury condition |
| US17 | `* *` | coach | sort players by an attribute | compare players in a useful order | Rating only; other attribute sorts are not implemented |
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

**Related user story:** US12

**MSS**

1. Coach requests to find a player by name.
2. CouchCoach displays the matching players.
3. Coach identifies a player from the displayed list and provides a numeric
   rating for that player.
4. CouchCoach records the rating, saves the updated data, and displays the
   player's recorded rating.

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

* 3b. The rating is missing, is not numeric, or violates the agreed rating rules.

    * 3b1. CouchCoach explains the rating requirements and leaves the data unchanged.

      Use case resumes at step 3.

* 4a. CouchCoach cannot save the updated data.

    * 4a1. CouchCoach informs the Coach that the rating was recorded in the
      current session but could not be saved and may be lost after closing.

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
including when the team is empty. Listing clears the search filter and displays all
remaining players in insertion order, with indices starting from 1.

| Test case | Expected result |
| --- | --- |
| `list` | Tan, Lim, and Nur displayed in that order, numbered 1, 2, and 3, with their recorded names and phone numbers. |
| `find wei`, then `list` | The two search results are replaced by all three players in their original order. |
| `find NobodyMatchesThisName`, then `list` | The empty search results are replaced by all three players. |
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

### Manual checks for sort scope and squad ordering

Use a disposable team: add Amy, Bob and Charlie in that order; rate Amy 0 and Bob 10;
keep Amy and Charlie, leaving Bob pending and Charlie unrated.

| Action | Expected result |
| --- | --- |
| `squad`, then `sort rating all` | Bob, Amy, Charlie; hidden pending players return and unrated Charlie is last. |
| `find Bob`, then `sort rating squad asc` | Amy, Charlie; the full kept squad replaces the search. |
| `sort rating asc all` | Amy, Bob, Charlie; direction before scope is accepted. |
| `sort rating`, then `squad` | Only kept players, retaining the active rating order. |
| `sort rating squad up`, `sort rating all squad` or `sort rating asc desc` | Usage error; data, current view and sorting are unchanged. |
| `list` | Amy, Bob, Charlie in insertion order. |
| Mark both kept players pending, then `sort rating squad` | Empty view without an error. |
| Clear the team, then `sort rating all asc` | Empty view without an error. |

Check the team file before and after read-only sort/view commands: its contents must not change.

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
| `filter i/injured`, then `find nur` | Nur appears even if the injury filter was empty; the name search considers the full roster. |
| `list`, `keep 3`, `squad`, then `find wei` | Tan and Lim appear even though neither is kept; `find` replaces the squad view. |
| `list`, `rate 1 2`, `rate 2 9`, `sort rating`, then `find wei` | Lim is displayed as 1, Tan as 2; the search preserves the active descending sort. |
| `tag 1 t/Captain` after the previous row, then `filter t/Captain` | Lim gains Captain and remains the sole match; Tan is unchanged. Filter replaces name search while retaining sorting. |
| `list` after a search | All four players displayed in original order. |
| `find wei`, then `delete 1` | Tan removed; Lim remains numbered 1; Nur and Muthu stay hidden. |
| `delete 1` again | Lim removed; filtered panel empty even though Nur and Muthu remain in the team. |
| `list`, `clear`, then `find wei` | Empty panel; `0 players listed!`; no failure on an empty team. |

Recreate the four-player fixture above before the storage checks. Before running successful and invalid searches, record the data file's contents and
modification time. Both must remain unchanged after every search. To check searches
with unavailable storage, rename the data directory after the team is loaded and
create a regular file named `data` in its place, blocking directory creation. Execute `find wei` and verify that
the normal search feedback still appears. Remove the temporary file and restore the
directory before adding or deleting players. Relaunch after preparing Muthu's record
and confirm that `find s/o` still finds it, checking that the slash survives storage.

After the table and storage checks, add disposable records `Tan Wei-Ming`, `Shaun O'Neil` and `Lim Jr.`. Verify `find Ming`, `find Neil` and `find Jr` find the appropriate records, while `find Min` does not. After all checks, close the app and remove the disposable folder.

See [Name search implementation](#name-search-implementation-f3-us04) for the parsing and predicate design.

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
| `tag 1 t/Captain t/captain` | Old labels are replaced by one Captain label, retaining the first spelling. The name search remains active. |
| `tag 1 t/CAPTAIN` | Spelling changes to CAPTAIN on the card and in feedback; this is an edit, not a no-change result. |
| `tag 1 t/` | Tags disappear; feedback says `Tag Tester: tags cleared.` Other player fields remain. |
| Repeat `tag 1 t/`. | `No changes made: Tag Tester already has these details.` The view and fields remain unchanged. A successful edit result still attempts a save. |
| `tag 1 t/ Trial   Group 2` | One label, `Trial Group 2`, with collapsed whitespace and preserved capitalisation. |
| `tag 1 t/Bad!` | Specific error naming `!`; tags and the previous view stay unchanged, and the file is not written. |
| `edit 1 p/789 t/Captain` | Guidance to use `tag`; the phone stays 456, tags and the previous view stay unchanged, and the file is not written. |
| Try `tag 1 t/ t/Striker`, then `tag 1 t/Striker t/`. | Each rejects the whole command with the empty-tag message; the label remains Trial Group 2. |
| Try `tag 99 t/Captain`, `tag 0 t/Captain`, and `tag t/Captain`. | Out-of-range index gives `The player index provided is invalid.`; malformed/missing indices show index/usage guidance. No data or view changes. |
| `tag 1 t/123456789012345678901234567890` | A 30-character label is accepted. |
| `tag 1 t/1234567890123456789012345678901` | Error names the 31-character label and 30-character limit; the old 30-character label remains. |
| `tag 1 t/Défenseur t/U18 & Under t/Centre Back/Wing` | All three valid labels appear in alphabetical order and remain after reload. |
| `tag 1 t/Striker`, then `filter t/Striker`, then `tag 1 t/Captain`. | Tag Tester disappears because the Striker filter remains active; the full roster is not restored. `list` shows Other Player and Tag Tester, with Captain on Tag Tester. |
| `find Tag`, then `rate 1 9`, `cut 1`, `pending 1`, `keep 1`, `remark 1 r/Improved passing`, `injury 1 i/fit`, and `edit 1 j/9 p/789`. | After each command Captain remains; the changed field updates without dropping unrelated fields. The final player is kept, fit, rated 9, jersey 9, phone 789, with the new remark. |
| `squad`, then `tag 1 t/Striker`. | Only Tag Tester is shown, still kept; changing tags retains the squad view and all assessment fields. |
| `tag 1 t/`, then exit and relaunch from the same folder. | Tags remain cleared; all other fields persist. Both players return in insertion order because the view is not persisted. |

For each rejected update, compare the data file contents and modification time before and after: neither should change. For successful updates, inspect the JSON and reload before accepting persistence as verified. Also run the sorted-index tag-update sequence under the filter tests below.

With the app closed, back up the disposable JSON. In separate copies, omit the `tags` field and then set it to `null`: the player loads without tags. Restore the backup between cases. An invalid tag string or a null entry inside the tag array must instead trigger the unreadable-data warning. Do not modify the only copy of real data. Remove the disposable test folder after closing the app and restoring any renamed paths.

### Filtering by tag (US16): manual regression checks

Use a fresh disposable folder, or clear only the disposable roster. Prepare players in this order:

```text
clear
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
| `filter t/STRIKER` | Amy only as 1; `1 player listed!`. |
| `filter t/Striker t/Captain t/striker` | Amy and Charlie once each, numbered 1 and 2; `2 players listed!`. |
| `filter t/strik`, then `filter t/NoSuchTag`. | Each is a successful empty list with `0 players listed!`. |
| `find Amy`, then `filter t/Goalkeeper`. | Bob appears despite being hidden by the name search. |
| `filter t/Captain`, then `find Bob`. | Bob appears; the tag predicate is replaced. |
| `squad`, then `filter t/Striker`. | Amy appears although not kept; the squad predicate is replaced. |
| `squad`, then `find Amy`. | Amy appears; name search also replaces the squad predicate. |
| `filter t/Captain`, then `squad`. | Bob and Charlie appear; Amy is hidden because she is not kept. |
| `filter i/INJURED`, then `filter t/Captain i/fit`. | First Amy only, then Charlie only. Tags use OR with each other and AND with injury status. |
| Try `filter t/`, `filter`, `filter Striker`, `filter t/Bad!`, `filter i/hurt`, `filter i/fit i/injured`, and `Filter t/Striker`. | Each reports the appropriate tag, format, status, duplicate-prefix or unknown-command error; Charlie remains the previous result and the file is not written. |
| `filter t/123456789012345678901234567890`, then the same label with one extra `1`. | The 30-character label searches successfully with no matches; 31 characters is rejected without changing that view. |
| `list`, `sort rating`, then `filter t/Captain`. | Charlie is 1 and Amy is 2: the active descending rating sort is retained, rather than roster order. |
| `tag 1 t/Goalkeeper` after the previous row. | Charlie is updated, not Amy; he disappears from Captain results. Amy remains as 1. |
| `find Charlie`, then `filter t/Goalkeeper`. | Bob and Charlie are both found; Charlie precedes unrated Bob under the active rating sort. |
| `filter t/Goalkeeper`, then `delete 2`. | Unrated Bob is deleted using the displayed index; Charlie remains as 1 and Amy stays hidden. |
| `add n/Dana p/444 t/Captain` | Full roster returns with the active sort: Charlie, Amy, Dana. The add clears the filter but retains sorting. |
| `list` | Amy, Charlie, Dana in insertion order, numbered 1–3; sorting is cleared. |
| `filter t/Captain`, then `sort rating all` | All three players return, in order Charlie, Amy, Dana; the explicit all scope replaces the tag filter. |
| `sort rating squad`, then `filter t/Captain`, then `list` | First Charlie only (the kept squad); next Amy and Dana (the tag filter replaces squad scope); finally all three in insertion order with sorting cleared. |
| `help`, then `filter t/Goalkeeper`. | Player panel returns showing Charlie; help includes current filter syntax. |
| Exit and relaunch from the same folder, then `filter t/Goalkeeper`. | Full insertion-order roster appears on launch; the subsequent filter finds Charlie with persisted Goalkeeper and rating 9. |
| `clear`, then `filter t/Striker`, then `find Amy`. | Both searches give `0 players listed!` without exceptions on an empty roster. |

Compare the JSON contents and modification time before and after valid, empty-result and invalid filters: neither changes. Block the save path using the procedure under [Finding players by name](#finding-players-by-name-f3-us04); filtering must still show ordinary results without a save error. Restore the data directory before modifying players or closing the test session. Close the app and remove only the disposable test data after completion.
