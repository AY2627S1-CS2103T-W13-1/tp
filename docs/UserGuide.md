---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# CouchCoach User Guide

CouchCoach is a **desktop app for coaches of any sports team to keep track of their players, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, CouchCoach lets you add, find and remove players faster than a clipboard, a spreadsheet or a typical mouse-driven app.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Check that Java `25` is installed. Open a terminal and run `java -version`; the first line should mention version `25`.<br>
   If it does not, install Java 25 by following [this guide](https://se-education.org/guides/tutorials/javaInstallation.html).<br>
   **Mac users:** install the exact Azul JDK 25 build *with JavaFX* (JDK+FX) prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html). CouchCoach is tested with this build.

1. Download `couchcoach.jar` from the **Assets** of the [latest release](https://github.com/AY2627S1-CS2103T-W13-1/tp/releases/latest).

1. Create a folder for CouchCoach that you can save files in, e.g. a `CouchCoach` folder in your home folder, and move `couchcoach.jar` into it.

1. Open a terminal, `cd` into that folder, and run `java -jar couchcoach.jar`. For example, on macOS or Linux:

   ```text
   cd ~/CouchCoach
   java -jar couchcoach.jar
   ```

   On Windows, use `cd %USERPROFILE%\CouchCoach` in Command Prompt instead of the first line.<br>
   A window like the one below should appear in a few seconds. The first time you run it, CouchCoach contains some sample players for you to try commands on. Type `clear` to remove them when you are ready to enter your own team.<br>
   <pic src="images/Ui.png" width="450" alt="CouchCoach window showing a team of players with their status, rating, jersey number, tags and remarks" />

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to see a summary of all commands.<br>
   Some example commands you can try:

   * `list` : Lists all players.

   * `add n/Tan Wei Ming p/91234567 j/10` : Adds a player named `Tan Wei Ming` with jersey number 10 to the team.

   * `delete 3` : Deletes the 3rd player shown in the current list.

   * `clear` : Deletes all players.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

<box type="warning" seamless>

**Launching CouchCoach:**

* CouchCoach keeps your team in a `data` folder inside the terminal's current folder (the _working directory_), not necessarily next to the JAR. Launch it from the same folder every time, or it will not find your team. See [Editing the data file](#editing-the-data-file) for details.
* CouchCoach must be able to save files in the folder you launch it from. If you launch it from a folder you cannot write to, it closes straight away, prints an error in the terminal, and no window appears. Move the JAR to a folder you own, e.g. in your home folder, and launch it from there.
* Double-clicking `couchcoach.jar` may also start CouchCoach on some computers, but it may then use a different Java version or a different working folder. Use the terminal command above so that the same team loads each time.
</box>

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `edit INDEX [n/NAME] [p/PHONE] [j/JERSEY] [t/TAG]...` can be used as `edit 1 n/Tan Wei Ming` or as `edit 1 p/91234567`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `find KEYWORD [MORE_KEYWORDS]...` can be used as `find wei` or as `find wei ming`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE`, `p/PHONE n/NAME` is also acceptable.

* Extraneous parameters for `help`, `list`, `squad`, and `exit` are ignored.<br>
  For example, `help 123` is interpreted as `help`.
  The destructive `clear` command must be entered on its own; extra text is rejected.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a summary of every command, with examples, in place of the player list.

Format: `help`

* You can also open the summary from the `Help` menu or by pressing `F1`.
* Type `list` to see your players again. Any successful player command also brings the list back.
* While the summary is shown, an index such as the `1` in `delete 1` still refers to the player list that was displayed before you opened help.
* If you type a command CouchCoach does not recognise, or press Enter on an empty command box, the result box shows `Unknown command. Type 'help' to see the list of commands.`


### Adding a player: `add`

Adds a player to the team.

Format: `add n/NAME p/PHONE [j/JERSEY] [t/TAG]...`

* Both `NAME` and `PHONE` must be given, in any order. `JERSEY` and `TAG` are optional.
* `NAME` can contain letters (including accented and non-English letters, e.g. `José García` or `张伟`), digits, spaces, and the characters `-` `'` `/` `.` `,` `(` `)`, and can be at most 100 characters long.
* `NAME` is saved with the capitalisation you type. Extra spaces at the start and end are removed, and repeated spaces inside the name are reduced to one.
* `PHONE` must have 3 to 15 digits and may start with a single `+`. Spaces, hyphens, dots and brackets are allowed, and are removed before saving, e.g. `8123 4567` is saved as `81234567` and `(65) 9123-4567` as `6591234567`. Letters, such as `ext 2`, are not allowed. To note what a number is for, e.g. `HP`, use [`remark`](#recording-a-player-observation-remark-us21).
* `JERSEY` must be a whole number from 0 to 99. Leading zeros are dropped, e.g. `j/07` is saved as `7`, `j/010` as `10`, and `j/099` as `99`.
  Only digits are accepted; signs and decimal points are invalid. Zero padding does not change
  the 0–99 range, so `j/0100` is rejected.
* Tags follow the [tag rules](#categorising-players-with-tags-us08).
* A player cannot be added if a player with the same name is already in the team. Names are compared ignoring capitalisation and repeated spaces, so `tan  WEI ming` counts as the same player as `Tan Wei Ming`, and the result box shows `This player already exists in the team.` Phone numbers are not compared, so two players may share a contact number, e.g. siblings who give a parent's number.
* If the new name looks like an existing player's name once spaces and punctuation are ignored, e.g. `TanWei Ming` and `Tan Wei Ming`, the player is still added, but the result box warns you so you can check it is not the same player.
* Players may share a jersey number, e.g. when trialists wear training bibs. The player is still added, but the result box warns you and names every other player with that number, including players hidden by an earlier `find` or `filter`.
* `n/`, `p/` and `j/` can each be given only once, e.g. `add n/Amy n/Bob p/91234567` is rejected, so that a typo does not save the wrong value.
* `add` cannot set a remark or injury status. Add the player first, then use [`remark`](#recording-a-player-observation-remark-us21) or [`injury`](#marking-a-player-s-injury-status-injury-us13).
* CouchCoach has no email or address fields. Text such as `e/amy@x.com` is read as part of the value before it, so `add n/Amy p/91234567 e/amy@x.com` is rejected with the phone number rules.
* A new player starts as **Pending**, **Unrated** and fit, with no remark.
* After adding, CouchCoach shows the full team again, even if the list was filtered by `find`, `filter` or `squad`, so that you can see the new player. The new player appears at the bottom of the list. This is also true while the list is sorted by rating, as new players are unrated and unrated players are listed last.

<box type="tip" seamless>

**Tip:** To add two players with the same name, add something to tell them apart, e.g. `n/Tan Wei Ming 2` or `n/Tan Wei Ming Jr`.
</box>

Examples, starting from a team that already contains `Nur'ain Binte Hassan` with jersey number 7:

| Command | Result box shows |
| --- | --- |
| `add n/Tan Wei Ming p/91234567` | `New player added: Tan Wei Ming; Phone: 91234567` |
| `add n/José García p/8123 4567 j/10 t/Striker` | `New player added: José García; Phone: 81234567; Jersey: 10; Tags: Striker` |
| `add p/(65) 9876-5432 n/Muthu s/o Ramasamy` | `New player added: Muthu s/o Ramasamy; Phone: 6598765432` |
| `add n/Lim Wei Jie p/98765432 j/07` | `New player added: Lim Wei Jie; Phone: 98765432; Jersey: 7`<br>`Warning: jersey number 7 is also used by Nur'ain Binte Hassan.` |
| `add n/TanWei Ming p/90000000` | `New player added: TanWei Ming; Phone: 90000000`<br>`Warning: this name looks like Tan Wei Ming. Check that this is not the same player.` |
| `add n/tan wei ming p/81112222` | `This player already exists in the team.` |
| `add n/Amy p/12` | The phone number rules; nothing is added. |
| `add n/Amy p/91234567 j/100` | `Jersey numbers should be whole numbers from 0 to 99, e.g. 7 or 23.` |
| `add n/Amy p/91234567 r/Strong left foot` | `add cannot set a remark or injury status. Use "remark INDEX r/REMARK" or "injury INDEX i/STATUS" instead.` |

### Categorising players with tags (US08)

Add your own labels for a position, role, season, or trial group with `t/TAG`:

* `add n/Amy Tan p/91234567 t/Striker t/Trial Group 2` adds Amy with two tags.
* `edit 1 t/Goalkeeper t/Captain` replaces the first displayed player's tags with these two labels.
* `edit 1 p/98765432` changes the phone while keeping existing tags.
* `edit 1 t/` removes all tags from the first displayed player.

Each tag must start with a letter or digit, and can contain letters (including accented and
non-English letters), digits, spaces, and the symbols `-` `&` `/` `_` `+` `'` `.`, e.g. `U18 & Under`,
`Centre Back/Wing` or `Défenseur`. Text that looks like another parameter, such as ` p/`, is
rejected, as it is usually a typo. A tag can contain up to 30 characters after surrounding spaces are
trimmed and repeated internal whitespace is collapsed. For example, `t/ Trial   Group 2`
is saved as `Trial Group 2`. Blank labels are invalid except for the single clearing
`edit ... t/` form. An invalid tag rejects the whole command, and the error names the problem,
e.g. the tag that is too long or the character that is not allowed.

Repeat `t/` for multiple tags. Duplicate labels are combined ignoring capitalisation,
so `t/Striker t/striker` keeps the first spelling, `Striker`, once. Tags appear in
alphabetical order on player cards and command feedback. They do not affect duplicate
player detection: the same name is still rejected even if tags differ.

Tags are saved automatically and restored when you restart. Older data files without
a `tags` field load with no tags. Tag edits use the displayed list's index and keep the
current search or filter, just like other edits.

Changing tags, names, or phone numbers keeps the player's selection status and rating.
The `keep`, `cut`, `pending`, and `rate` commands keep existing tags. `squad` displays
kept players with their tags and ratings.

### Listing all players: `list`

Shows every player and clears any filter from a previous `find` or `filter` command.

Format: `list`

This command also clears rating sorting. Players appear in the order they were added, numbered from 1. The result box shows
`Listed all players`, even when the team is empty. This command does not change player data.
Any text after `list` is ignored, so `list all` has the same effect as `list`.

Indices refer to positions in the currently displayed list and can change after deletion.
Run `list` again before using an index if you need to see the full team.

### Sorting players by rating: `sort`

Orders the displayed players by their performance rating.

Format: `sort rating [asc|desc]`

* `sort rating` and `sort rating desc` show the highest rating first.
* `rating`, `asc` and `desc` can be typed in any letter case, e.g. `sort Rating ASC`.
  The command word `sort` itself must be lower case.
* `sort rating asc` shows the lowest rating first. **Unrated** players appear last
  in both directions; a score of `0` still appears before unrated players.
* Players with the same rating keep their original insertion order.
* Sorting keeps the current `find` filter. Later searches use the active sort order too.
* Indices are renumbered to match the sorted display. `rate`, `edit` and `delete` use
  these displayed indices. Check the list again after a rating change, as the player may move.
* The active sort remains when adding or editing players; new players appear at their
  sorted position. `add` still clears the search filter as usual, while `edit` keeps it.
* `list` clears the filter and sorting, restoring the full team in insertion order.
* Sorting an empty team is allowed. Invalid fields, directions or extra arguments are
  rejected without changing the current list.
* Sorting changes no saved player data and does not save. After a restart the list
  returns to insertion order.

Example: after `rate 1 3` and `rate 2 9`, `sort rating` shows the player rated 9 before
those rated 3 or Unrated. `sort rating asc` puts the player rated 3 first.

### Rating a player: `rate`

Sets or removes a player's performance rating and saves it automatically.

Format: `rate INDEX RATING`

* `INDEX` is the positive whole number shown beside the player in the current list,
  including a list filtered by `find`.
* `RATING` must be a whole number from **0 to 10**, inclusive. Decimal values, signs,
  leading zeros such as `08`, extra arguments and values outside this range are rejected.
* To remove a rating, type `unrated` as `RATING`, in any letter case. The player becomes
  **Unrated** again and appears with the unrated players when sorting. If the player is
  already unrated, the result box says so and nothing changes.
* New players and players saved before ratings were introduced start as **Unrated**.
  A rating of `0` is a real rating and is different from Unrated.
* The rating appears on the player card and is retained when their name or phone is edited.
* Rating a player keeps the current search filter. An invalid command changes nothing.
* Ratings are saved with the rest of your team and restored when you reopen CouchCoach.

Examples:

* `rate 1 8` gives the first displayed player a rating of 8/10.
* `find Tan` followed by `rate 1 10` gives the first search result a rating of 10/10.
* `rate 1 unrated` removes the first displayed player's rating.
* `rate 1 11` shows `Rating must be a whole number from 0 to 10. To remove a rating, use: rate INDEX unrated`

### Marking a player's injury status: `injury` (US13)

Marks a player as injured or fit again, independently of whether they are kept, cut or pending.

Format: `injury INDEX i/STATUS`

* `INDEX` is the positive whole number beside a player in the current displayed list,
  including a list filtered by `find`, `filter` or `squad`.
* `STATUS` must be `injured` or `fit`, ignoring case. For example, `i/INJURED` is accepted.
* New players and players loaded from older files without injury status default to `fit`.
* Use `i/` exactly once. An unsupported value, duplicate prefix or invalid index rejects
  the whole command and leaves the team unchanged.
* The current filter and all other player details are retained. Injury status is also
  preserved when you change a remark, name, phone, tags, rating or selection status.
* Injured players have an **Injured** badge next to their name, so they stand out in the list.
  Fit players have no badge. The status is saved automatically and restored when you reopen CouchCoach.
* If the player already has that status, CouchCoach tells you so and nothing changes.
* To see only injured (or fit) players, use [`filter i/injured`](#filtering-players-by-tag-filter-us16).

Examples:

* `injury 1 i/injured` marks the first displayed player as injured.
* `injury 1 i/fit` marks that player fit again.
* After `find Amy`, `injury 1 i/INJURED` updates the first search result.

### Recording a player observation: `remark` (US21)

Sets or replaces a player's remark. An empty remark clears the observation from their card.

Format: `remark INDEX r/REMARK`

* `INDEX` is the positive whole number beside a player in the current displayed list,
  including a list filtered by `find`, `filter` or `squad`.
* Use `r/` exactly once. A second `r/` after a space is treated as another delimiter and
  rejects the command, even within an observation: `remark 1 r/See r/soccer for drills`
  is invalid. Write `remark 1 r/See soccer for drills` instead. Other prefix-like text
  such as `n/coach` can appear in the remark.
* Leading and trailing spaces are removed. `remark 1 r/` clears the remark.
* The current filter is retained. Invalid syntax or an invalid index leaves the team unchanged.
* Remarks are saved automatically, restored when CouchCoach reopens, and preserved by
  name, phone, tag, rating, selection-status and injury-status updates. Older files start with empty remarks.

Examples:

* `remark 1 r/Strong left foot` records an observation for the first displayed player.
* `remark 1 r/Improved passing` replaces that observation.
* `remark 1 r/` clears it.
* After `find Amy`, `remark 1 r/Strong left foot` updates the first search result.

### Editing a player: `edit`

Changes the name, phone number, jersey number or tags of a player in the displayed list.

Format: `edit INDEX [n/NAME] [p/PHONE] [j/JERSEY] [t/TAG]...`

* Edits the player at the specified `INDEX`, the number shown beside the player in the displayed list. `INDEX` must be a positive whole number such as 1, 2 or 3. An index that is `0` or larger than the list shows `The player index provided is invalid.`
* At least one of the optional fields must be provided; `edit 1` on its own shows `At least one field to edit must be provided.`
* Each value you give replaces the current one. Names, phone numbers and jersey numbers follow the same rules as in [`add`](#adding-a-player-add), and `n/`, `p/` and `j/` can each be given only once, e.g. `edit 1 n/Amy n/Bob` is rejected.
* A player cannot be renamed to another player's name: names are compared as in `add`, and the result box shows `This player already exists in the team.` Changing only the capitalisation or spacing of a player's own name, e.g. `Tan Wei Ming` to `TAN WEI MING`, is allowed.
* If the new name looks like another player's, e.g. `TanWei Ming` when `Tan Wei Ming` exists, the change is made, but the result box warns you, as with `add`. Editing other details does not repeat this warning.
* Omit `j/` to keep the player's current jersey number. Type `j/` with nothing after it to remove the jersey number. An empty `j/` works only with `edit`; `add` rejects it.
* If the edit includes `j/` and the new jersey number is already used by another player, the change is still made and the result box shows the same warning as in `add`, naming players hidden by an earlier `find` or `filter` too. An edit without `j/`, e.g. one that only changes the phone number, does not repeat the warning.
* Repeat `t/TAG` to replace all of the player's tags. Omit `t/` to keep them. Use a single empty `t/` to clear them; combining empty and non-empty tags is rejected.
* `edit` cannot change a remark or injury status. Use [`remark`](#recording-a-player-observation-remark-us21) or [`injury`](#marking-a-player-s-injury-status-injury-us13) instead.
* Details you do not edit stay as they are. In particular, editing never changes a player's selection status, rating, remark or injury status.
* The displayed list stays as it is, so you can keep editing the results of a `find` or `filter` with their numbers unchanged. An active rating sort also stays. If the edited player no longer matches the search, e.g. after a name change, they drop out of the displayed list; use `list` to see everyone.
* The result box shows the player's new details, and notes any jersey number or tags you removed. If the values you typed are the same as the current ones, including a phone number typed with different spacing, it says `No changes made: NAME already has these details.`

Examples, for a team where the 1st player is `Tan Wei Ming`, with jersey number 10, and `Nur'ain Binte Hassan` wears 7:

| Command | Result box shows |
| --- | --- |
| `edit 1 p/9123 4567` | `Edited player: Tan Wei Ming; Phone: 91234567; Jersey: 10` |
| `edit 1 n/Tan Wei Ming Jr` | `Edited player: Tan Wei Ming Jr; Phone: 91234567; Jersey: 10` |
| `edit 1 j/7` | `Edited player: Tan Wei Ming Jr; Phone: 91234567; Jersey: 7`<br>`Warning: jersey number 7 is also used by Nur'ain Binte Hassan.` |
| `edit 1 j/` | `Edited player: Tan Wei Ming Jr; Phone: 91234567 (jersey number removed)` |
| `edit 1 j/` again | `No changes made: Tan Wei Ming Jr already has these details.` |
| `edit 0 n/Amy` | `The player index provided is invalid.` |

### Finding players by name: `find`

Filters the displayed list to players whose names match any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]...`

* Supply at least one keyword. Separate keywords with spaces; repeated spaces are accepted.
* The search is case-insensitive: `find wei` matches `Tan Wei Ming`.
* Only whole words match: `find Wei` matches `Tan Wei Ming`, while `find We` does not.
* Only names are searched. A phone number matches only if it is also a whole word in a player's name.
* A player matching **any** keyword is shown (an `OR` search). Keyword order does not matter.
* Each matching player appears once, even when several keywords match or a keyword is repeated.
* A word joined by punctuation such as `-`, `'`, `.` or `/` matches as a whole, and so does each part
  between the punctuation: `find Wei-Ming` and `find Ming` both match `Tan Wei-Ming`, `find Neil` matches
  `Shaun O'Neil`, `find Jr` matches `Lim Jr.`, and `find s/o` matches `Muthu s/o Ramasamy`. Each part
  must still match in full, so `find Min` does not match `Tan Wei-Ming`.
* No tagged parameters are used; `find n/Wei` searches for the literal word `n/Wei`.
* Each search searches the whole team, including players hidden by an earlier search.
* Results follow the active rating sort, or insertion order when no sort is active and are numbered from 1. After `find`, `delete 1`
  deletes the first displayed match. The search filter remains active after deletion.
* Use `list` to restore the full team list.
* Searching changes neither player records nor the saved data file.

For a team containing `Tan Wei Ming`, `Lim Wei Jie`, `Nur Aisyah`, and `Muthu s/o Ramasamy`:

| Command | Displayed players | Result message |
| --- | --- | --- |
| `find wei` | Tan Wei Ming, Lim Wei Jie | `2 players listed!` |
| `find TAN` | Tan Wei Ming | `1 player listed!` |
| `find wei nur` | Tan Wei Ming, Lim Wei Jie, Nur Aisyah | `3 players listed!` |
| `find ming tan ming` | Tan Wei Ming, once | `1 player listed!` |
| `find We` | Empty list | `0 players listed!` |
| `find s/o` | Muthu s/o Ramasamy | `1 player listed!` |

Entering `find` without keywords, including `find` followed only by spaces, shows:

```text
Invalid command format!
find: Finds players whose names contain any of the given keywords.
Parameters: KEYWORD [MORE_KEYWORDS]...
Example: find wei ming
```

The displayed list and player data remain unchanged after this error. Command words are
case-sensitive: use `find`, as `Find` is unrecognized.

### Filtering players by tag: `filter` (US16)

Show a group of players by the custom tags assigned using `add` or `edit`, by injury status, or both.

Format: `filter [t/TAG]... [i/STATUS]`

* Supply at least one `t/TAG` or an `i/STATUS`. `STATUS` is `injured` or `fit`, ignoring case, e.g. `filter i/injured` shows only injured players.
* With both, a player must match both, e.g. `filter t/Striker i/fit` shows fit strikers.
* Tags must be non-empty. Labels follow the [tag rules](#categorising-players-with-tags-us08).
* Match complete labels, ignoring capitalisation: `filter t/striker` matches `Striker`,
  while `filter t/strik` does not. `filter t/Trial Group 2` treats the whole label as one tag.
* Repeat `t/` to match **any** requested tag (OR). A player matching multiple tags is listed once.
* Filter the whole team, including players hidden by a previous `find`, `filter`, or `squad`.
  Results keep roster order and are numbered from 1.
* `delete` and `edit` use the displayed indices and keep the filter; a successful `add`
  restores the full list. `find` replaces the tag filter with its name search.
* `keep`, `cut`, `pending`, and `rate` also use displayed indices and preserve tags.
  `squad` replaces the tag filter with the kept-player view.
* `list` restores the full roster. Filtering changes no player data and does not save.
* Missing tags, invalid labels, or text before the first `t/` show an error and leave the
  current list unchanged. A tag with no matches is valid and displays `0 players listed!`.

For Amy tagged `Striker` and `Captain`, Bob tagged `Goalkeeper`, and Charlie tagged `Captain`:

| Command | Displayed players | Result message |
| --- | --- | --- |
| `filter t/STRIKER` | Amy | `1 player listed!` |
| `filter t/Striker t/Captain` | Amy, Charlie | `2 players listed!` |
| `filter t/Goalkeeper` | Bob | `1 player listed!` |
| `filter t/strik` | Empty list | `0 players listed!` |

Use the lowercase command word `filter`; `Filter` is unrecognized. `filter` without
parameters displays the format error and syntax example. `filter t/` is rejected
because a tag cannot be empty; it does not clear anything.

### Deleting a player: `delete`

Deletes the specified player from the team.

Format: `delete INDEX`

* Deletes the player at the specified `INDEX`.
* The index refers to the index number shown in the displayed player list, which may be filtered by `find` or `filter`.
* The index **must be a positive whole number** 1, 2, 3, ...
* The remaining players are renumbered after a deletion, so check the list again before deleting another player.

Examples:
* `list` followed by `delete 2` deletes the 2nd player in the team.
* `find Tan` followed by `delete 1` deletes the 1st player in the results of the `find` command.

### Marking a player as kept, cut or pending: `keep`, `cut`, `pending`

Records your selection decision for a player. The decision is shown as a badge next to the player's name: **Pending**, **Kept** or **Cut**.

Format: `keep INDEX`, `cut INDEX` or `pending INDEX`

* `keep` marks the player at the specified `INDEX` as kept, and `cut` marks them as cut.
* `pending` marks the player as not yet decided, e.g. to undo a `keep` or `cut` you made by mistake.
* Every player you add starts as pending. Editing a player with `edit` does not change their status.
* The index refers to the index number shown in the displayed player list, which may be filtered by `find`.
* The index **must be a positive whole number** 1, 2, 3, ...
* The displayed list stays as it is, so you can keep working through the results of a `find`.
* If the player already has that status, CouchCoach tells you so and nothing changes.

Examples:
* `list` followed by `keep 2` marks the 2nd player in the team as kept.
* `find Tan` followed by `cut 1` marks the 1st player in the results of the `find` command as cut.
* `pending 1` changes the 1st player in the displayed list back to pending.

### Viewing your squad: `squad`

Shows only the players you have marked as kept, so you can see your squad at a glance.

Format: `squad`

* The players are shown in the same order as in `list`.
* The result box shows how many players are in your squad, e.g. `3 players in your squad.`
* If you have not kept anyone yet, the list is empty and the result box tells you how to keep a player.
* `squad` shows your whole squad, even if you ran `find` before it.
* While you are viewing your squad, a player you `cut` or mark as `pending` disappears from the list, as they are no longer in your squad.
* Use `list` to see all players again.

Examples:
* `keep 1` followed by `keep 3` and then `squad` shows the two players you kept.

### Clearing all players: `clear`

Deletes all players from the team.

Format: `clear`

Use `clear` on its own. Extra arguments, such as `clear 1` or `clear everything`, show a usage error and leave all players unchanged.
To remove one player, use `delete INDEX` instead.

<box type="warning" seamless>

**Caution:** `clear` cannot be undone and does not ask for confirmation.
</box>

### Exiting the program: `exit`

Closes CouchCoach. Every change that was saved successfully is kept. `exit` does not save, so if a change could not be saved, save it before exiting (see [Saving the data](#saving-the-data)); otherwise that change is lost.

Format: `exit`

### Saving the data

CouchCoach saves your data automatically after every change (`add`, `edit`, `rate`, `remark`, `injury`, `delete`, `keep`, `cut`, `pending` and `clear`). You do not need to save manually. `list`, `find`, `filter`, `sort`, `squad`, `help` and `exit` do not change your data, so they do not save.

If a change cannot be saved (e.g. the data folder was deleted or made read-only), the result box shows `Your change was applied but could not be saved to disk. Fix the problem before closing CouchCoach, or the change will be lost.` The change is still shown in CouchCoach, but it is not on disk yet.

Fixing the problem does not save the change by itself, and `exit` does not save either. To save it:

1. Fix the problem, e.g. restore the data folder or make it writable again.
1. Make another change. Every save writes your whole team, so the next successful change also saves the change that failed earlier. If you have nothing else to change, add a temporary player and then delete it, e.g. `add n/Temp Player p/000` followed by `delete INDEX`, using the temporary player's index.
1. Check that the result box shows the normal success message, not the save error, before you exit.

### Editing the data file

CouchCoach data is saved automatically as a JSON file `[working directory]/data/addressbook.json`.
The working directory is the terminal's current folder when you launch the app; it can differ from the folder containing the JAR.
For example, if your terminal is in `/teams/trials` and you run `java -jar /apps/couchcoach.jar`, the team file is `/teams/trials/data/addressbook.json`.
Launch from the same working directory each time to load the same team. The status bar shows the data file path.
Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, CouchCoach starts with an empty team at the next run and shows a warning in the result box. The invalid file stays on disk until your next change (`add`, `edit`, `rate`, `remark`, `injury`, `delete`, `keep`, `cut`, `pending` or `clear`), which replaces it, so back up the file before editing it.<br>
A file containing two players with the same name, ignoring capitalisation and repeated spaces, also counts as invalid.<br>
Furthermore, certain edits can cause CouchCoach to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous CouchCoach home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE [j/JERSEY] [t/TAG]...` <br> e.g., `add n/Tan Wei Ming p/91234567 j/10 t/Striker`
**Clear**  | `clear`
**Cut**    | `cut INDEX`<br> e.g., `cut 3`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE] [j/JERSEY] [t/TAG]...`<br> e.g., `edit 2 n/Lim Wei Jie`, `edit 3 j/23`; remove a jersey number with `edit 3 j/`
**Exit**   | `exit`
**Filter** | `filter [t/TAG]... [i/STATUS]`<br> e.g., `filter t/Striker t/Captain`, `filter i/injured`
**Find**   | `find KEYWORD [MORE_KEYWORDS]...`<br> e.g., `find wei ming`
**Injury** | `injury INDEX i/STATUS`<br> e.g., `injury 1 i/injured`; mark fit again with `injury 1 i/fit`
**Sort**   | `sort rating [asc|desc]`<br> e.g., `sort rating desc`
**Rate**   | `rate INDEX RATING`<br> e.g., `rate 1 8`; remove with `rate 1 unrated`
**Remark** | `remark INDEX r/REMARK`<br> e.g., `remark 1 r/Strong left foot`; clear with `remark 1 r/`
**Keep**   | `keep INDEX`<br> e.g., `keep 3`
**List**   | `list`
**Pending** | `pending INDEX`<br> e.g., `pending 3`
**Squad**  | `squad`
**Help**   | `help`
