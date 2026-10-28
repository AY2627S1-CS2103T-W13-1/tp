---
  layout: default.md
  title: "Testing guide"
  pageNav: 3
---

# Testing guide

<!-- * Table of Contents -->
<page-nav-print />

<!-- -------------------------------------------------------------------------------------------------------------------- -->

## Running tests

Use JDK 25, matching `build.gradle` and CI. Confirm the Gradle JVM with `./gradlew --version` (`gradlew.bat --version` on Windows). JavaFX tests need a graphical session; on headless Linux, prefix the command with `xvfb-run -a` as CI does.

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<box type="info" seamless>

**Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</box>

--------------------------------------------------------------------------------------------------------------------

Before pushing, run `./gradlew check coverage` and `./.github/run-checks.sh` from the repository root. `check` includes the JUnit suite and Checkstyle; `coverage` writes JaCoCo reports to `build/reports/jacoco/coverage/`. Repository-wide scripts inspect staged files, so stage the intended changes before running them. On Windows, use `gradlew.bat check coverage`; CI runs the POSIX repository checks on Linux.

Automated tests do not replace launching the candidate JAR. Follow the [manual testing appendix](DeveloperGuide.md#appendix-instructions-for-manual-testing) with a disposable working directory. Verify the current command syntax against the [User Guide](UserGuide.md), including displayed indices, save failures, restart persistence and empty/invalid input.

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.util.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`
