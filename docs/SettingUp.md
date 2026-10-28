---
  layout: default.md
  title: "Setting up and getting started"
  pageNav: 3
---

# Setting up and getting started

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Setting up the project on your computer

<box type="warning" seamless>

**Caution:** Follow the steps below precisely. The setup may fail if you skip or change a step.
</box>

First, **fork** [the CouchCoach repository](https://github.com/AY2627S1-CS2103T-W13-1/tp), and **clone** your fork onto your computer. Install JDK 25 using the platform-specific instructions linked in the [User Guide](UserGuide.md#quick-start).

From the repository root, confirm that `java -version` and `./gradlew --version` both use Java 25 (`gradlew.bat --version` in Windows Command Prompt). Use the checked-in Gradle wrapper; a separate Gradle installation is unnecessary. Set `JAVA_HOME` to the JDK 25 installation if Gradle selects another version.

If you plan to use IntelliJ IDEA (highly recommended):

1. **Configure the JDK**: Follow the guide [_[se-edu/guides] IDEA: Configuring the JDK_](https://se-education.org/guides/tutorials/intellijJdk.html) to ensure Intellij is configured to use **JDK 25**, and select the same JDK for the Gradle JVM.
1. **Import the project as a Gradle project**: Follow the guide [_[se-edu/guides] IDEA: Importing a Gradle project_](https://se-education.org/guides/tutorials/intellijImportGradleProject.html) to import the project into IDEA.
   <box type="warning" seamless>
   Note: Importing a Gradle project is slightly different from importing a normal Java project.
   </box>
1. **Verify the setup**:
   1. Run `seedu.address.Main` and try a few commands.
   1. [Run the tests](Testing.md) to ensure they all pass.

--------------------------------------------------------------------------------------------------------------------

## Before writing code

1. **Configure the coding style**

   If using IDEA, follow the guide [_[se-edu/guides] IDEA: Configuring the code style_](https://se-education.org/guides/tutorials/intellijCodeStyle.html) to set up IDEA's coding style to match ours.

   <box type="tip" seamless>

   **Tip:**
    Optionally, follow the guide [_[se-edu/guides] Using Checkstyle_](https://se-education.org/guides/tutorials/checkstyle.html) to learn how to use Checkstyle in IDEA, for example to report problems _as_ you write code.
   </box>

1. **Set up CI**

   This project includes GitHub Actions configuration files in `.github/workflows`. Java CI checks each push and pull request on Ubuntu, macOS and Windows. Enable Actions in your fork if GitHub prompts you. If required checks remain at **Expected** without runs, check that the team repository's Java CI workflow is enabled; see [CI](DevOps.md#continuous-integration-ci).

1. **Learn the design**

   When you are ready to start coding, we recommend that you get some sense of the overall design by reading about [CouchCoach’s architecture](DeveloperGuide.md#architecture).

1. **Do the tutorials**
   These tutorials will help you get acquainted with the codebase.

   * [Tracing code](https://se-education.org/guides/tutorials/ab3TracingCode.html)
   * [Adding a new command](https://se-education.org/guides/tutorials/ab3AddRemark.html)
   * [Removing fields](https://se-education.org/guides/tutorials/ab3RemovingFields.html)
