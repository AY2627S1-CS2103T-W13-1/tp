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

1. Open a terminal, `cd` into that folder, and run `java -jar "couchcoach.jar"`. For example, on macOS or Linux:

   ```text
   cd ~/CouchCoach
   java -jar "couchcoach.jar"
   ```

   On Windows Command Prompt, use `cd /d "%USERPROFILE%\CouchCoach"` instead of the first line. In PowerShell, use `cd "$env:USERPROFILE\CouchCoach"`.<br>
   If your downloaded JAR has a different name, substitute that name in the launch command.
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
* CouchCoach saves your team in the folder you launch it from, so launch it from a folder you can write to, e.g. one in your home folder. If you launch it from a folder you cannot write to, CouchCoach still opens, but every change shows a warning that it could not be saved, and the change is lost when you exit. Close CouchCoach, move the JAR to a folder you own, and launch it from there.
* Double-clicking `couchcoach.jar` may also start CouchCoach on some computers, but it may then use a different Java version or a different working folder. Use the terminal command above so that the same team loads each time.
</box>

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `edit INDEX [n/NAME] [p/PHONE] [j/JERSEY]` can be used as `edit 1 n/Tan Wei Ming` or as `edit 1 p/91234567`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `find KEYWORD [MORE_KEYWORDS]...` can be used as `find wei` or as `find wei ming`.

* Separate prefixes from preceding text with spaces, tabs, or non-breaking spaces copied from other apps.
  Pasted tabs are converted to ordinary spaces in the command box, including tabs inside a value. Pressing the Tab key moves keyboard focus.
  Leading and trailing non-breaking spaces around field values are ignored, while those within a value are preserved.
  Line breaks do not act as prefix separators.

* Command words and prefixes must be lowercase: use `add n/Amy p/123`, not `Add N/Amy P/123`. Values follow the rules of their individual fields.

* Prefixed parameters can be in any order; positional arguments such as `rate INDEX RATING` must stay in the order shown.<br>
  For example, if the command specifies `n/NAME p/PHONE`, `p/PHONE n/NAME` is also acceptable.

* Extraneous parameters for `help`, `list`, `squad`, and `exit` are ignored.<br>
  For example, `help 123` is interpreted as `help`.
  `clear` and `save` must each be entered on their own; extra text is rejected.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Understanding the window and player numbers

The command box is at the top of the window; the result box below it confirms a command or explains an error. The player list shows names, contact numbers, ratings, selection badges, and any jersey numbers, tags, remarks or injury badges. Scroll the list to see more players. The bottom status bar shows the team file path.

`INDEX` always means the number beside a player in the **current displayed list**, starting at 1. It is not a permanent player ID. Searching, filtering, sorting, deleting or updating a player can change those numbers. Check the list before every indexed command. Positive forms such as `+1` and `01` also identify player 1; zero, negative numbers and numbers beyond the displayed list are invalid.

`find`, `filter` and `squad` each replace the previous filter. They retain any rating sort; `list` shows everyone again and clears sorting. These views do not delete hidden players. Player details persist after a successful save, while filters and sorting reset on restart.

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
* Tags follow the [tag rules](#tagging-a-player-tag-us08).
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

<a id="categorising-players-with-tags-us08"></a>

### Tagging a player: `tag` (US08)

Use custom labels to group players by position, role, season or trial group. Add labels with `add`, or replace an existing player's labels with `tag`.

Format for replacing tags: `tag INDEX t/TAG [t/TAG]...`

Format for clearing tags: `tag INDEX t/`

`INDEX` is the positive whole number beside the player in the displayed list, including search results, tag/injury filters and the squad. Recheck the displayed numbers after changing a view or a player. Supplying tags replaces the entire old set; include every label you want to keep. Name, phone and jersey edits preserve the existing tags. A single empty `t/` clears all tags; mixing an empty label with other labels is invalid.

Separate parameter prefixes with spaces, pasted tabs or non-breaking spaces, as described in the command-format notes.

Examples:

* `add n/Amy Tan p/91234567 t/Striker t/Trial Group 2` adds Amy with two tags.
* `tag 1 t/Goalkeeper t/Captain` replaces the first displayed player's tags with these two labels.
* `edit 1 p/98765432` changes the phone while keeping existing tags.
* `tag 1 t/` removes all tags from the first displayed player.

Each tag must start with a letter or digit, and can contain letters (including accented and
non-English letters), digits, spaces, and the symbols `-` `&` `/` `_` `+` `'` `.`, e.g. `U18 & Under`,
`Centre Back/Wing` or `Défenseur`. Text that looks like another parameter, such as ` p/`, is
rejected, as it is usually a typo. A tag can contain up to 30 characters after surrounding spaces are
trimmed and repeated internal whitespace is collapsed. For example, `t/ Trial   Group 2`
is saved as `Trial Group 2`. Blank labels are invalid except for the single clearing
`tag INDEX t/` form. An invalid tag rejects the whole command, and the error names the problem,
e.g. the tag that is too long or the character that is not allowed.

Repeat `t/` for multiple tags. Duplicate labels are combined ignoring capitalisation,
so `t/Striker t/striker` keeps the first spelling, `Striker`, once. Tags appear in
alphabetical order on player cards and command feedback. They do not affect duplicate
player detection: the same name is still rejected even if tags differ.

Tags are saved automatically and restored when you restart. Older data files without a `tags` field load with no tags. Changing tags keeps the current search, tag/injury filter, squad view and active rating sort. If the player no longer matches the tag filter, they disappear from that view and the remaining players are renumbered. Use `list` to see everyone in insertion order again.

Changing only tags preserves the player's name, phone, jersey number, rating, selection status, remark and injury status. Name, phone, jersey, rating, selection, remark and injury updates also preserve tags unless you explicitly replace or clear them with `tag`.

For a player named Amy Tan with phone 91234567 and no jersey number:

| Command | Result message |
| --- | --- |
| `tag 1 t/Captain t/Striker` | `Updated tags for Amy Tan: Captain, Striker` |
| Repeat the same command | `No changes made: Amy Tan already has these details.` |
| `tag 1 t/` | `Amy Tan: tags cleared.` |
| Repeat `tag 1 t/` | `No changes made: Amy Tan already has these details.` |
| `tag 1 t/Bad!` | `The tag "Bad!" contains "!", which is not allowed. Tags can contain letters, digits, spaces and the symbols - & / _ + ' .` |

Changing only a tag's capitalisation, e.g. from `STRIKER` to `Striker`, updates its displayed and saved spelling. A malformed index, a missing index or an index outside the displayed list rejects the update. For an out-of-range index the result is `The player index provided is invalid.` Invalid tags reject the whole tag update. The old `edit INDEX t/...` syntax is rejected with guidance to use `tag`, including mixed name/phone/jersey-and-tag edits; no field changes. Use separate commands. After a rejected command, the data and view remain unchanged. If saving fails after a valid tag update, follow [Saving the data](#saving-the-data) before closing the app.

### Listing all players: `list`

Shows the whole team, including players hidden by a name search, tag/injury filter or squad view.

Format: `list`

* Clears both the current filter and rating sorting. Remaining players appear in the order they were added, numbered from 1. Editing a player does not change their place in this order.
* The result box shows `Listed all players`, including when the roster is empty. Listing does not restore deleted players or change any player details.
* Extra arguments are ignored: `list all` and `list n/Tan` have the same effect as `list`. Use the lowercase command word; `List` is not recognised.
* Listing does not save player data. It works even if the data file is currently unwritable.

After `sort rating`, run `list` to restore insertion order. Check the newly displayed numbers before using an index in another command.

### Sorting players by rating: `sort`

Orders the players currently displayed so you can compare their performance ratings.

Format: `sort rating [all|squad] [asc|desc]`

* `sort rating` and `sort rating desc` show the highest rating first; `sort rating asc` shows the lowest first.
* **Unrated** players are last in both directions. A score of **0** is a real rating and appears before every unrated player.
* Equal scores keep the order in which those players were added. Unrated players also keep insertion order, even when you switch directions or update a player.
* `rating`, `all`, `squad`, `asc` and `desc` are case-insensitive, e.g. `sort Rating ALL ASC`. The command word `sort` must be lowercase. Scope and direction can appear in either order, so `sort rating asc squad` also works.
* With no scope, sorting keeps the current view: after `find`, `filter` or `squad`, only the displayed players are sorted.
* `sort rating all` replaces any name, tag, injury or squad filter with the full roster, then sorts it. `sort rating squad` replaces the filter with all kept players, including kept players hidden by the previous view.
* The scope selects the view for this command; it does not restrict later searches. A subsequent `find`, `filter` or `squad` replaces that view while keeping the rating sort.
* Displayed indices are renumbered. Commands such as `rate`, `edit`, `tag`, `delete`, `keep`, `cut`, `pending`, `remark` and `injury` act on these current positions. Recheck the list after a change removes or moves a player.
* Sorting an empty view succeeds. Missing/unsupported fields, invalid or repeated options, conflicting scopes/directions and extra arguments show a format error and leave the previous view and order unchanged. For example, `sort name`, `sort rating up`, `sort rating all squad` and `sort rating asc desc` are invalid.
* Sorting does not change or save player records or stored roster order. After restarting from the same working directory, the full team appears in insertion order; filters and sorting are not restored.

Without an explicit scope, the result box shows `Sorted players by rating (descending); unrated players last` or the corresponding `ascending` message. Scoped sorting names the chosen group: `Sorted all players by rating (descending); unrated players last` or `Sorted kept players by rating (ascending); unrated players last`.

| Next command | Effect while a rating sort is active |
| --- | --- |
| `add` | Shows the full roster again, keeping the sort. New players start Unrated and appear after the rated players and earlier unrated players. |
| `edit` | Keeps the sort and current filter. A renamed player may disappear if they no longer match a name search. |
| `tag` | Keeps the sort and current filter. A player may disappear if their replacement tags no longer match a tag filter. |
| `rate` | Keeps the filter and sort; the player moves to their new rated/unrated position. Other displayed indices can change. |
| `find` | Searches the whole team, replacing the old filter; matches use the active sort. |
| `filter` | Replaces the old filter with the requested tag/injury conditions; matches use the active sort. |
| `squad` | Shows all kept players using the active sort, even if a previous search hid them. |
| `list` | Shows the full roster and clears sorting, restoring insertion order. |

For example, add Amy, Bob and Cy in that order, then give them ratings 0, 10 and 10. `sort rating` shows Bob, Cy, Amy; `sort rating asc` shows Amy, Bob, Cy. Any unrated players follow them in insertion order in both cases.

### Rating a player: `rate`

Sets or removes a player's performance rating and saves it automatically.

Format: `rate INDEX RATING`

* `INDEX` is the positive whole number beside the player in the **current displayed list**, including a sorted list, name search, tag/injury filter or squad view. It must identify a player in that view.
* `RATING` is a whole number from **0 to 10**, inclusive. Signs (`+8`, `-1`), decimals (`8.0`), leading zeros (`08`), values above 10, missing arguments and extra arguments are rejected.
* Use `unrated` in any letter case to remove the rating, e.g. `rate 1 UNRATED`. **Unrated** means not assessed; **0** means assessed with a score of zero. New players and players loaded from files without a rating start Unrated.
* Rating changes preserve the player's name, phone, selection status, tags, remark, injury status and jersey number.
* The current filter and sort stay active. A rating change can move the player in a sorted list, so check the displayed numbers before rating another player. Removing a rating moves the player among the unrated players in insertion order.
* Invalid input leaves both player data and the current view unchanged. An index outside the displayed list shows `The player index provided is invalid.`
* Successfully saved ratings and removals survive a restart. If saving fails, the change remains visible in this session and the result box warns that it could not be saved; follow [Saving the data](#saving-the-data) before closing the app.

Examples below assume the first displayed player is Amy:

| Command | Feedback / result |
| --- | --- |
| `rate 1 0` | `Rated player: Amy; Rating: 0/10` |
| `rate 1 10` | `Rated player: Amy; Rating: 10/10` |
| `rate 1 unrated` | `Removed the rating of player: Amy; Rating: Unrated` |
| `rate 1 unrated` again | `Amy is already unrated.`; player details stay unchanged. |
| `rate 1 11` | `Rating must be a whole number from 0 to 10. To remove a rating, use: rate INDEX unrated` |

After `find Tan`, `rate 1 8` rates the first displayed match rather than the first player in the full roster. The same displayed-index rule applies after filtering or sorting.

### Marking a player's injury status: `injury` (US13)

Marks a player as injured or fit again, independently of whether they are kept, cut or pending.

Format: `injury INDEX i/STATUS`

* `INDEX` is the positive whole number beside a player in the current displayed list,
  including a list filtered by `find`, `filter` or `squad`.
* `STATUS` must be `injured` or `fit`, ignoring case. For example, `i/INJURED` is accepted.
* New players and players loaded from older files without injury status default to `fit`.
* Use `i/` exactly once. An unsupported value, duplicate prefix or invalid index rejects
  the whole command and leaves the team unchanged.
* The current filter and rating sort are retained. If an injury change means the player no longer matches an injury filter, they disappear and the remaining players are renumbered. All other player details are retained. Injury status is also
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
* Use `r/` exactly once. A second `r/` after a space, tab or non-breaking space is treated as another delimiter and
  rejects the command, even within an observation: `remark 1 r/See r/soccer for drills`
  is invalid. Write `remark 1 r/See soccer for drills` instead. Other prefix-like text
  such as `n/coach` can appear in the remark.
* Leading and trailing spaces are removed. `remark 1 r/` clears the remark.
* The current filter and rating sort are retained. Invalid syntax or an invalid index leaves the team unchanged.
* Remarks are saved automatically, restored when CouchCoach reopens, and preserved by
  name, phone, tag, rating, selection-status and injury-status updates. Older files start with empty remarks.

Examples:

* `remark 1 r/Strong left foot` records an observation for the first displayed player.
* `remark 1 r/Improved passing` replaces that observation.
* `remark 1 r/` clears it.
* After `find Amy`, `remark 1 r/Strong left foot` updates the first search result.

### Editing a player: `edit`

Changes the name, phone number or jersey number of a player in the displayed list.

Format: `edit INDEX [n/NAME] [p/PHONE] [j/JERSEY]`

* Edits the player at the specified `INDEX`, the number shown beside the player in the displayed list. `INDEX` must be a positive whole number such as 1, 2 or 3. An index that is `0` or larger than the list shows `The player index provided is invalid.`
* At least one of the optional fields must be provided; `edit 1` on its own shows `At least one field to edit must be provided.`
* Each value you give replaces the current one. Names, phone numbers and jersey numbers follow the same rules as in [`add`](#adding-a-player-add), and `n/`, `p/` and `j/` can each be given only once, e.g. `edit 1 n/Amy n/Bob` is rejected.
* A player cannot be renamed to another player's name: names are compared as in `add`, and the result box shows `This player already exists in the team.` Changing only the capitalisation or spacing of a player's own name, e.g. `Tan Wei Ming` to `TAN WEI MING`, is allowed.
* If the new name looks like another player's, e.g. `TanWei Ming` when `Tan Wei Ming` exists, the change is made, but the result box warns you, as with `add`. Editing other details does not repeat this warning.
* Omit `j/` to keep the player's current jersey number. Type `j/` with nothing after it to remove the jersey number. An empty `j/` works only with `edit`; `add` rejects it.
* If the edit includes `j/` and the new jersey number is already used by another player, the change is still made and the result box shows the same warning as in `add`, naming players hidden by an earlier `find` or `filter` too. An edit without `j/`, e.g. one that only changes the phone number, does not repeat the warning.
* Tags are retained. Use `tag INDEX t/TAG...` to replace them, or `tag INDEX t/` to clear them.
  Supplying `t/` to `edit` rejects the whole command and shows the new syntax.
* `edit` cannot change a remark or injury status. Use [`remark`](#recording-a-player-observation-remark-us21) or [`injury`](#marking-a-player-s-injury-status-injury-us13) instead.
* Details you do not edit stay as they are. In particular, editing never changes a player's selection status, rating, remark or injury status.
* The current filter and rating sort remain active. If the edited player no longer matches a name search, they disappear and the remaining players are renumbered. Recheck the displayed indices before another edit; use `list` to see everyone.
* The result box shows the player's new details, and notes any jersey number you removed. If the values you typed are the same as the current ones, including a phone number typed with different spacing, it says `No changes made: NAME already has these details.`

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
* Each search searches the whole team, including players hidden by an earlier `find`, `filter` or `squad`. It replaces that view rather than adding another condition.
* Results follow the active rating sort, or insertion order when no sort is active, and are numbered from 1. After `find`, `delete 1`
  deletes the first displayed match. The search filter remains active after deletion.
* A later `filter` replaces the name search; `squad` replaces it with all kept players. Both retain active sorting. Use `list` to restore the full team list and clear sorting.
* A valid keyword with no matches, including a search on an empty team, shows an empty list and `0 players listed!`.
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

<a id="filtering-players-by-tag-filter-us16"></a>

### Filtering players by tag or injury status: `filter` (US16)

Show a group of players by the custom tags assigned using `add` or `tag`, by injury status, or both.

Format: `filter [t/TAG]... [i/STATUS]`

* Supply at least one `t/TAG` or an `i/STATUS`. `STATUS` is `injured` or `fit`, ignoring case, e.g. `filter i/injured` shows only injured players.
* With both, a player must match both, e.g. `filter t/Striker i/fit` shows fit strikers.
* Tags must be non-empty. Labels follow the [tag rules](#tagging-a-player-tag-us08).
* Match complete labels, ignoring capitalisation: `filter t/striker` matches `Striker`,
  while `filter t/strik` does not. `filter t/Trial Group 2` treats the whole label as one tag.
* Repeat `t/` to match **any** requested tag (OR). A player matching multiple tags is listed once.
* Separate parameter prefixes with spaces, pasted tabs or non-breaking spaces, as described in the command-format notes.
* Filter the whole team, including players hidden by a previous `find`, `filter`, or `squad`.
  Results follow the active rating sort, or insertion order when no sort is active,
  and are numbered from 1. Check these displayed indices before editing or deleting.
  Each new filter replaces the previous view rather than adding conditions to it.
* `delete` and `edit` use the displayed indices and keep the filter; a successful `add`
  restores the full list. `find` replaces the tag filter with its name search.
* `keep`, `cut`, `pending`, and `rate` also use displayed indices and preserve tags.
  `squad` replaces the tag filter with the kept-player view.
* `list` restores the full roster and clears sorting, returning players to insertion order.
  Filtering changes no player data and does not save. Filters and sorting are not restored after restarting; your saved players and their tags are restored.
* Missing conditions, invalid labels/statuses, duplicate `i/` arguments, or text before the first recognised parameter show an error and leave the current list and data unchanged. An injury-only filter is valid. A tag with no matches is valid and displays `0 players listed!`, including on an empty team.

For Amy tagged `Striker` and `Captain`, Bob tagged `Goalkeeper`, and Charlie tagged `Captain`,
added in that order with no active sort:

| Command | Displayed players | Result message |
| --- | --- | --- |
| `filter t/STRIKER` | Amy | `1 player listed!` |
| `filter t/Striker t/Captain` | Amy, Charlie | `2 players listed!` |
| `filter t/Goalkeeper` | Bob | `1 player listed!` |
| `filter t/strik` | Empty list | `0 players listed!` |

For example, if Amy and Bob both have the `Striker` tag and ratings of 2 and 9 respectively,
`sort rating` followed by `filter t/Striker` shows Bob first and Amy second. `list` clears
that sort and restores the full team in insertion order.

Before running `list` in that example, `tag 1 t/Goalkeeper` updates Bob and removes him from the Striker results, because the filter remains active. Amy becomes the first displayed match. Recheck indices after an update.

Use the lowercase command word `filter`; `Filter` is unrecognized. `filter` without
parameters displays the format error and syntax example. `filter t/` is rejected
because a tag cannot be empty; it does not clear anything.

With no conditions, the result box shows:

```text
Invalid command format!
filter: Lists players with any of the given tags (exact labels, ignoring case), the given injury status, or both.
Parameters: [t/TAG]... [i/STATUS] (at least one; STATUS is injured or fit)
Examples: filter t/Striker t/Trial Group 2, filter i/injured, filter t/Striker i/fit
```

`filter t/Bad!` shows the same specific tag error as the tag-update example above. `filter t/` instead explains that a tag cannot be empty and gives `tag INDEX t/` as the clearing syntax. To remove the view filter while keeping player tags, use `list`.

### Deleting a player: `delete`

Removes a player from the team permanently.

Format: `delete INDEX`

* `INDEX` is the number shown beside the player in the list on screen right now. That list may be
  narrowed by [`find`](#finding-players-by-name-find), [`filter`](#filtering-players-by-tag-filter-us16)
  or [`squad`](#viewing-your-squad-squad), or reordered by [`sort rating`](#sorting-players-by-rating-sort);
  `delete` always uses the number you can see.
* The result box confirms who was removed, e.g. `Deleted player: Amy Tan; Phone: 91234567; Tags: Striker`.
* The list on screen stays as it is: a search, tag filter, squad view or rating sort remains active.
  The players below the deleted one move up, so **their numbers change**. Check the list again before
  deleting another player.
* The deletion is saved straight away (see [Saving the data](#saving-the-data)).
* If `INDEX` is missing or is not a whole number, e.g. `delete`, `delete x` or `delete 1 2`, the result box
  shows the correct format. If it is `0`, negative or larger than the list on screen, it shows
  `The player index provided is invalid.` Nothing is deleted in either case.

<box type="warning" seamless>

**Caution:** `delete` cannot be undone and does not ask for confirmation. To remove every player at once,
use [`clear`](#clearing-all-players-clear).
</box>

Examples:
* `list` followed by `delete 2` deletes the 2nd player in the team.
* `find Tan` followed by `delete 1` deletes the 1st player in the search results. The search stays on screen,
  so the next `Tan` player is now number 1.
* `sort rating` followed by `delete 1` deletes the highest-rated player.

### Marking a player as kept, cut or pending: `keep`, `cut`, `pending`

Records your selection decision for a player. The decision appears as a badge next to the player's name:
**Pending**, **Kept** or **Cut**.

Format: `keep INDEX`, `cut INDEX` or `pending INDEX`

* `keep` marks the player as kept, and `cut` marks them as cut. `pending` marks them as not yet decided,
  e.g. to undo a `keep` or `cut` made by mistake.
* Every new player starts as pending. Players from data files saved before v1.4, which have no decision
  recorded, also load as pending.
* `INDEX` is the number shown beside the player in the list on screen right now, including a list narrowed by
  `find`, `filter` or `squad`, or reordered by `sort rating`.
* The result box confirms the change, e.g. `Marked Amy Tan as kept.` If the player already has that decision,
  it says so instead, e.g. `Amy Tan is already marked as kept.`, and nothing changes.
* The list on screen stays as it is, so you can work through the results of a `find` or `filter`.
  While viewing your squad, though, a player you `cut` or mark `pending` leaves the squad, so they disappear
  from the list and the players below them move up one number.
* A decision does not change a player's rating, tags or position in a rating sort. Editing a player with `edit`
  does not change their decision.
* The decision is saved straight away and restored when you reopen CouchCoach.
* If `INDEX` is missing or is not a whole number, e.g. `keep` or `keep a`, the result box shows the correct format.
  If it is `0`, negative or larger than the list on screen, it shows `The player index provided is invalid.`

Examples:
* `list` followed by `keep 2` marks the 2nd player in the team as kept.
* `find Tan` followed by `cut 1` marks the 1st player in the search results as cut.
* `pending 1` changes the 1st player in the list on screen back to pending.

### Viewing your squad: `squad`

Shows only the players you have marked as kept, so you can see your squad at a glance.

Format: `squad`

* Players keep the active rating sort, or insertion order if no sort is active.
  Use `sort rating squad` to sort the whole kept squad directly (see [`sort`](#sorting-players-by-rating-sort)); `list` clears sorting.
* The result box shows how many players are in your squad, e.g. `3 players in your squad.`
  If you have not kept anyone yet, the list is empty and the result box says
  `No players in your squad yet. Use 'keep INDEX' to keep a player, or 'list' to see all players.`
* `squad` shows your whole squad, even if a `find` or `filter` was active before it.
* While viewing your squad, a player you `cut` or mark `pending` disappears from the list. A player you
  `delete` is removed from the team.
* Use `list` to see all players again. `squad` changes no data and does not save.
* Any text after `squad` is ignored, so `squad all` is the same as `squad`.

Examples:
* `keep 1` followed by `keep 3` and then `squad` shows the two players you kept.
* `sort rating` followed by `squad` shows your kept players, highest rating first.

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

CouchCoach saves your team automatically after every successful change. You do not need to save manually.

* These commands save: `add`, `edit`, `tag`, `rate`, `remark`, `injury`, `delete`, `keep`, `cut`, `pending` and `clear`.
  Each save writes your whole team to the data file. You can also use `save` without changing players.
* These commands never save, as they do not change your data: `list`, `find`, `filter`, `sort`, `squad`,
  `help` and `exit`.
* A command that is rejected, e.g. because of a wrong format or an invalid index, changes nothing and does not save.

If a change cannot be saved (e.g. the data file or its folder is read-only, the disk is full,
or a directory occupies the data file path),
the result box shows the applied command's feedback followed by a warning that the change
could not be saved. The change is still shown in CouchCoach, but it is not on disk yet.
The data file may be unchanged, empty or partially written; a failed save does not guarantee that the
previous file remains intact. If the `data` folder is deleted while CouchCoach is open, it is recreated
at the next save.
The command box clears because the operation has already happened. **Do not repeat the
original command**: for example, repeating `delete 1` would delete the next player.

Fixing the problem does not save the change by itself, and `exit` does not save either. To save it:

1. Fix the problem, e.g. make the data file or its folder writable, free up disk space, or remove the directory occupying the data file path.
1. Enter `save` to write the current team without changing any players or their displayed order.
1. Check for `Saved the current team to disk.` before you exit. If saving still fails, fix
   the problem and retry `save`; unsaved changes remain in memory until you close CouchCoach.

### Retrying a save: `save`

Writes the **whole team**, including players hidden by the current view, to disk without reapplying a previous command. The current search,
filter and rating sort are retained. Saving an empty team writes an empty roster.

Format: `save`

Use `save` on its own; extra arguments are rejected. This command is useful after a save
failure, and can also be used at any time to save the current team. It replaces the data
file with the whole team held in memory, so back up any external file edits first. On success, the result is `Saved the current team to disk.` If it still cannot save, the result is `The current team could not be saved to disk. Fix the problem and type 'save' to retry before closing CouchCoach, or unsaved changes will be lost.`

### Editing the data file

CouchCoach keeps your team in a JSON file, `data/addressbook.json`, inside the **working directory**: the folder
your terminal is in when you launch the app. This can differ from the folder containing the JAR. For example,
if your terminal is in `/teams/trials` and you run `java -jar /apps/couchcoach.jar`, the team file is
`/teams/trials/data/addressbook.json`. Launch from the same folder each time to load the same team.
The status bar at the bottom of the window shows the data file path.

What CouchCoach does with the file when it starts:

* **No data file:** it starts with a sample team, so you can try the commands. The file is created at your
  first change or explicit `save`. To start empty instead, use `clear`.
* **A valid data file:** it loads your team. Data files from older versions still load: a player without a
  saved decision is pending, without a rating is unrated, without an injury status is fit, and without
  tags, a remark or a jersey number has none.
* **An invalid data file:** it starts with an empty team and the result box shows
  `The data file is not in a readable format. CouchCoach has started with an empty team. Your next change or 'save' will replace the file with the current team. Back up the file before continuing.`
  The invalid file is left untouched while you only use commands that do not save (see [Saving the data](#saving-the-data)),
  so you can close CouchCoach, fix or restore the file, and reopen it. Your next change or explicit `save`
  replaces the file with the current team.

Advanced users can edit the file directly while CouchCoach is closed. Each player is stored with these fields:

Field | Allowed values | If missing
------|----------------|-----------
`name` | Same rules as `n/NAME` in [`add`](#adding-a-player-add) | Required
`phone` | Digits only, optionally starting with `+`, 3 to 15 digits (no spaces) | Required
`selectionStatus` | `PENDING`, `KEPT` or `CUT`, in any letter case | Pending
`rating` | `"0"` to `"10"`, a plain whole number from `0` to `10`, or `null`; the app writes rated values as strings | Unrated
`tags` | A list of labels following the [tag rules](#tagging-a-player-tag-us08) | No tags
`remark` | Any text | No remark
`injuryStatus` | `fit` or `injured`, in any letter case | Fit
`jerseyNumber` | A digit string or plain whole number from `0` to `99`, e.g. `"7"` or `7`; the app writes strings, and leading zeros are accepted in strings | No jersey number

<box type="warning" seamless>

**Caution:**
If your edits make the file invalid, CouchCoach starts with an empty team (see above), and your next change or explicit `save`
replaces the invalid file, so **back up the file before editing it**.<br>
These all make the file invalid: a value outside the allowed values above, a missing `name` or `phone`,
a malformed JSON file, an empty file, a file without a `persons` list, a `null` entry in the `persons` list,
or two players with the same name, ignoring capitalisation and repeated spaces.<br>
A file containing only `null` also counts as invalid and uses the same empty-team recovery.
</box>

### Application preferences

Window settings are saved in `[working directory]/preferences.json`.
Invalid preferences, including a file containing only `null` or `null` window settings,
are replaced with defaults at startup. Your team data remains unchanged.

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: Can I use CouchCoach without an internet connection?<br>
**A**: Yes. Once Java and the JAR are installed, all player commands and the in-app help work offline. Keep only one CouchCoach instance open for a given team file; simultaneous edits from different instances can overwrite each other.

**Q**: How do I transfer my data to another computer?<br>
**A**: Close CouchCoach on both computers. Copy `data/addressbook.json` from the folder you launch CouchCoach from
on the old computer (see [Editing the data file](#editing-the-data-file)) into a `data` folder inside the folder
you will launch it from on the new computer, replacing any file there. Then launch CouchCoach from that folder.

**Q**: CouchCoach says my data file is not in a readable format. How do I get my team back?<br>
**A**: Do not make any change or use `save` yet: either replaces the unreadable file with the current team.
Close CouchCoach, then either restore your backup of `data/addressbook.json`, or fix the file using the rules in
[Editing the data file](#editing-the-data-file). Reopen CouchCoach and check that your team is back.

**Q**: The result box says my change could not be saved. Is it lost?<br>
**A**: Not while CouchCoach is still open. Follow the steps in [Saving the data](#saving-the-data) to save it
before you exit.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, moving the application to a secondary screen and later disconnecting that screen can leave the saved window position off-screen. Close CouchCoach, then delete `preferences.json` in the working directory before launching again. This resets window settings without deleting the team file.
1. **On Linux with Wayland**, if JavaFX fails at startup with `Gdk-CRITICAL`, try `GDK_BACKEND=x11 java -jar "couchcoach.jar"` from the same working directory.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE [j/JERSEY] [t/TAG]...` <br> e.g., `add n/Tan Wei Ming p/91234567 j/10 t/Striker`
**Clear**  | `clear`
**Cut**    | `cut INDEX`<br> e.g., `cut 3`
**Delete** | `delete INDEX`<br> e.g., `delete 3` (cannot be undone)
**Edit**   | `edit INDEX [n/NAME] [p/PHONE] [j/JERSEY]`<br> e.g., `edit 2 n/Lim Wei Jie`, `edit 3 j/23`; remove a jersey number with `edit 3 j/`
**Exit**   | `exit`
**Filter** | `filter [t/TAG]... [i/STATUS]`<br> e.g., `filter t/Striker t/Captain`, `filter i/injured`, `filter t/Striker i/fit`
**Find**   | `find KEYWORD [MORE_KEYWORDS]...`<br> e.g., `find wei ming`
**Injury** | `injury INDEX i/STATUS`<br> e.g., `injury 1 i/injured`; mark fit again with `injury 1 i/fit`
**Tag**    | `tag INDEX t/TAG [t/TAG]...`<br> e.g., `tag 1 t/Striker`, `tag 1 t/`
**Sort**   | `sort rating [all\|squad] [asc\|desc]`<br> e.g., `sort rating` (current view), `sort rating squad asc`
**Rate**   | `rate INDEX RATING` (0–10 or `unrated`)<br> e.g., `rate 1 8`; remove with `rate 1 unrated`
**Remark** | `remark INDEX r/REMARK`<br> e.g., `remark 1 r/Strong left foot`; clear with `remark 1 r/`
**Keep**   | `keep INDEX`<br> e.g., `keep 3`
**List**   | `list` — show all players in insertion order; clear filters and sorting
**Pending** | `pending INDEX`<br> e.g., `pending 3` (undoes a keep or cut)
**Save**   | `save`
**Squad**  | `squad`<br> shows only kept players
**Help**   | `help`
