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

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

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
| `* *` | coach | give players numeric ratings, see who remains unrated, and sort players by rating | compare assessed players on a common scale while identifying those who still need evaluation |
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
3. Coach identifies a player from the displayed list and submits the details
   to change.
4. CouchCoach updates the selected player's details, saves the updated data,
   and displays the updated details.

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

* 3b. The edit request contains invalid details or does not specify any
  details to change.

    * 3b1. CouchCoach explains the input error and leaves the data unchanged.

      Use case resumes at step 3.

* 3c. The requested name duplicates another player's name under the
  duplicate-name rule.

    * 3c1. CouchCoach reports the duplicate name and leaves the data unchanged.

      Use case resumes at step 3.

* 4a. CouchCoach cannot save the updated data.

    * 4a1. CouchCoach informs the Coach that the edits were applied in the
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

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Adding a player

1. Adding a player with valid details

   1. Test case: `add n/Tan Wei Ming p/91234567`<br>
      Expected: `Tan Wei Ming` appears at the bottom of the list. The status message shows `New player added: Tan Wei Ming; Phone: 91234567`.

   1. Test case: `add p/+65 9876-5432 n/Nur'ain   Binte Hassan`<br>
      Expected: The player is added as `Nur'ain Binte Hassan` with phone `+6598765432`. Parameter order does not matter, repeated spaces in the name are collapsed, and spaces and hyphens in the phone are removed.

   1. Test case: `add n/Muthu s/o Ramasamy p/8123 4567`<br>
      Expected: The player is added. The `/` in the name is accepted and the phone is stored as `81234567`.

1. Adding a player while the list is filtered

   1. Prerequisites: Filter the list using `find`, e.g. `find Tan`.

   1. Test case: `add n/Lim Wei Jie p/98765432`<br>
      Expected: The full list is shown again, with `Lim Wei Jie` at the bottom.

1. Adding a duplicate player

   1. Prerequisites: `Tan Wei Ming` is in the list.

   1. Test case: `add n/tan  WEI ming p/81112222`<br>
      Expected: No player is added. The status message shows `This player already exists in the team.`

   1. Test case: `add n/Tan Wei Ming 2 p/91234567`<br>
      Expected: The player is added, because only the name is checked for duplicates.

1. Adding a player with invalid details

   1. Test case: `add n/Tan Wei Ming`<br>
      Expected: No player is added. The status message shows the invalid command format error with the `add` usage.

   1. Test case: `add n/Tan @ Ming p/91234567`<br>
      Expected: No player is added. The status message shows the name constraints.

   1. Test case: `add n/Amy p/12`<br>
      Expected: No player is added. The status message shows the phone constraints.

   1. Other incorrect add commands to try: `add`, `add n/ p/91234567`, `add n/Amy p/9123ABCD`, `add n/Amy p/91234567 e/amy@x.com`, a name longer than 100 characters<br>
      Expected: No player is added, and an error message is shown.

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
names. Updating the model's filtered list replaces the previous predicate, so each
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
