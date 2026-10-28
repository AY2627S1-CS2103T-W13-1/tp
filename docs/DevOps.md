---
  layout: default.md
  title: "DevOps guide"
  pageNav: 3
---

# DevOps guide

<!-- * Table of Contents -->
<page-nav-print />

<!-- -------------------------------------------------------------------------------------------------------------------- -->

## Build automation

This project uses Gradle for **build automation and dependency management**. **We recommend reading [this Gradle tutorial from se-edu/guides](https://se-education.org/guides/tutorials/gradle.html).**


The following commands perform common Gradle tasks.


* **`clean`**: Deletes the files created during the previous build tasks (e.g. files in the `build` folder).<br>
  For example: `./gradlew clean`

* **`shadowJar`**: Uses the Shadow plugin to create the fat JAR file `build/libs/couchcoach.jar`.<br>
  For example: `./gradlew shadowJar`

* **`run`**: Builds and runs the application.<br>
  **`runShadow`**: Builds the application as a fat JAR, then runs it.

* **`checkstyleMain`**: Runs the code style check for the main code base.<br>
  **`checkstyleTest`**: Runs the code style check for the test code base.

* **`test`**
  * `./gradlew test`: Runs all tests.
  * `./gradlew clean test`: Cleans the project before running all tests

--------------------------------------------------------------------------------------------------------------------

## Continuous integration (CI)

This project uses GitHub Actions; its workflows are in `.github/workflows`. **Java CI** runs `check coverage` with JDK 25 (Zulu JDK+FX) on Ubuntu, macOS and Windows. Linux also runs the repository-wide checks and Gradle uses `xvfb-run -a` for JavaFX tests. **MarkBind Action** builds and deploys the website after a push to `master`.

The required PR checks are `build (ubuntu-latest)`, `build (macos-latest)` and `build (windows-latest)`. If they remain **Expected — Waiting for status to be reported** with no workflow run, check that Java CI is enabled in the team repository. Re-enable it if necessary, then trigger a new PR event, such as another commit. If runs exist, inspect their job logs to distinguish queued jobs from failing steps.

### Code coverage

As part of CI, Gradle generates JaCoCo coverage reports from the tests, and CI uploads the coverage data to Codecov. Codecov then provides information about test coverage.

However, because Codecov is known to run into intermittent problems (e.g., report upload fails) due to issues on the Codecov service side, the CI is configured to pass even if the Codecov task failed. Therefore, developers are advised to check the code coverage levels periodically and take corrective actions if the coverage level falls below desired levels.

To enable Codecov for forks of this project, follow the steps given in [this se-edu guide](https://se-education.org/guides/tutorials/codecov.html).

### Repository-wide checks

In addition to Gradle checks, CI runs repository-wide checks. Unlike Gradle checks, which cover files used in the build, these checks cover every repository file and enforce rules that are hard to apply on development machines, such as line-ending requirements.

These checks are implemented as POSIX shell scripts, and thus can only be run on POSIX-compliant operating systems such as macOS and Linux. To run all checks locally on these operating systems, execute the following in the repository root directory:

`./.github/run-checks.sh`

Any warnings or errors will be printed out to the console.

**If adding new checks:**

* Checks are implemented as executable `check-*` scripts within the `.github` directory. The `run-checks.sh` script will automatically pick up and run files named as such. That is, you can add more such files if you need and the CI will do the rest.

* Check scripts should print out errors in the format `SEVERITY:FILENAME:LINE: MESSAGE`
  * SEVERITY is either ERROR or WARN.
  * FILENAME is the path to the file relative to the current directory.
  * LINE is the line of the file where the error occurred and MESSAGE is the message explaining the error.

* Check scripts must exit with a non-zero exit code if any errors occur.

--------------------------------------------------------------------------------------------------------------------

## Making a release

Make the release from the team's reviewed `master` revision. For the v1.5 release candidate, follow the [Week 11 deliverables](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week11/project.html): publish the runnable JAR and current online UG/DG, and smoke-test the JAR before the deadline.

1. Confirm that intended feature and documentation PRs are merged and all required Java CI checks pass. Verify the version in [`MainApp.java`](https://github.com/AY2627S1-CS2103T-W13-1/tp/blob/master/src/main/java/seedu/address/MainApp.java) matches the intended release (e.g. `V1.5`). Review and commit any version change before tagging.
1. With JDK 25 selected, run `./gradlew clean check coverage shadowJar` and the [repository checks](#repository-wide-checks). On headless Linux, prefix the Gradle command with `xvfb-run -a`.
1. [Build the documentation](Documentation.md#building-and-previewing-the-site). Inspect UG, DG, diagrams, navigation and the current app screenshot. Confirm the deployed website matches the release revision.
1. Copy `build/libs/couchcoach.jar` into an empty writable folder. Run `java -version`, then `java -jar "couchcoach.jar"`. Follow the [manual testing appendix](DeveloperGuide.md#appendix-instructions-for-manual-testing), checking a data change, `save`, exit and reload from that folder. Use disposable data.
1. Tag the tested commit with the release version, e.g. `v1.5`, and push that tag to the team repository. [Create the GitHub release](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository) from that tag and attach the tested `couchcoach.jar`; avoid spaces or special characters in the filename.
1. Download the uploaded asset and repeat the launch/reload smoke test. Check that the website's download link resolves to this release. Review remaining milestone issues/PRs with the team before rescheduling or closing them and closing the milestone.
