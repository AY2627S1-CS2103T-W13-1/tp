---
  layout: default.md
  title: ""
---

# CouchCoach

[![Java CI](https://github.com/AY2627S1-CS2103T-W13-1/tp/actions/workflows/gradle.yml/badge.svg)](https://github.com/AY2627S1-CS2103T-W13-1/tp/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/AY2627S1-CS2103T-W13-1/tp/branch/master/graph/badge.svg)](https://codecov.io/gh/AY2627S1-CS2103T-W13-1/tp)

**Run trials and pick your squad from the keyboard.** CouchCoach is a desktop app for coaches of school, club and community sports teams who have many players to keep track of during trials and team selection.

<!-- Ui.png was captured from master at 066c17c2 (v1.5 candidate) on 28 Oct 2026, using a disposable sample team. -->
<pic src="images/Ui.png" width="600" alt="CouchCoach showing seven players sorted by rating, with their selection status, injury status, jersey numbers, tags and remarks" />

A clipboard or spreadsheet gets slow once a trial has dozens of players: details are scattered, finding one player means scrolling, and it is hard to see who you have already decided on. CouchCoach keeps every player in one list that you control by typing short commands, so you can record and compare players as fast as you can type.

## What you can do

* **Record every player** with a name, contact number and optional jersey number. CouchCoach stops you adding the same player twice, and warns you about shared jersey numbers and names that look alike.
* **Organise and search** with your own tags, such as positions or trial groups. Find players by name, or filter them by tag or injury status.
* **Assess players** by rating them from 0 to 10, sorting by rating, noting observations as remarks, and marking injuries.
* **Make selection decisions** by marking each player as kept, cut or pending, then viewing just your squad.
* **Keep your data safe and private.** Every change is saved straight away to a file on your own computer. No account or internet connection is needed.

## Get started

CouchCoach runs on Windows, macOS and Linux with Java 25.

* **Download** `couchcoach.jar` from the [latest release](https://github.com/AY2627S1-CS2103T-W13-1/tp/releases/latest).
* **Set it up** by following the [Quick start](UserGuide.html#quick-start), then learn every command in the [**User Guide**](UserGuide.html).
* **Develop CouchCoach** further with the [**Developer Guide**](DeveloperGuide.html).

## Acknowledgements

* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)
