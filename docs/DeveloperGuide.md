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
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

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

**Value proposition**: Manage contacts faster than with a typical mouse-driven GUI application.


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

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
