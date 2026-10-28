# CouchCoach

[![Java CI](https://github.com/AY2627S1-CS2103T-W13-1/tp/actions/workflows/gradle.yml/badge.svg)](https://github.com/AY2627S1-CS2103T-W13-1/tp/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/AY2627S1-CS2103T-W13-1/tp/branch/master/graph/badge.svg)](https://codecov.io/gh/AY2627S1-CS2103T-W13-1/tp)

CouchCoach is a desktop app for sports coaches who need a quick way to keep
track of players during trials and team selection. It combines a command box
for fast keyboard use with a graphical list of player records.

## What you can do

The current version lets you:

- Add players with a name, phone number, optional jersey number, and tags.
- Add or clear custom tags, such as positions or trial groups.
- List players, find them by name, or filter by tags and injury status.
- Edit or delete a player using their number in the displayed list.
- Rate players, sort ratings across the current view, full team or kept squad,
  and distinguish unrated players from a score of zero.
- Record remarks, mark injuries, and decide whether players are kept, cut or
  pending; use `squad` to view kept players.
- Clear the list when starting a new set of records.
- Save changes automatically to a local JSON file and retry failed saves
  with `save` without repeating the original change.

## Get started

For the packaged app, download `couchcoach.jar` from the
[latest release](https://github.com/AY2627S1-CS2103T-W13-1/tp/releases/latest)
and follow the [User Guide quick start](docs/UserGuide.md#quick-start).

To run from source, install JDK 25, clone this repository, and run from the
repository root:

```bash
./gradlew run
```

Enter commands in the command box. For example:

```text
add n/Tan Wei Ming p/91234567 t/goalkeeper
find Tan
list
```

Type `help` to open the in-app help page. Player data is saved in
`data/addressbook.json` relative to the directory from which you run the app.

For command details, see the [User Guide](docs/UserGuide.md). For setup,
architecture, and testing information, see the
[Developer Guide](docs/DeveloperGuide.md).

## Development

Run the project checks with Java 25:

```bash
./gradlew check
```

CouchCoach is built with Java and JavaFX. It uses Gradle for builds and Jackson
for local data storage.

## Acknowledgements

CouchCoach is based on [AddressBook Level 3](https://github.com/se-edu/addressbook-level3)
by the [SE-EDU initiative](https://se-education.org/).
