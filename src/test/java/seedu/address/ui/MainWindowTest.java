package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.JerseyNumber;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainWindowTest {
    @TempDir
    public Path testDirectory;

    private MainWindow window;
    private Model model;
    private TextField commandField;
    private TextArea resultDisplay;
    private StackPane playerArea;

    @Test
    public void execute_sortEmptySquadThroughCommandBox_showsEmptyList() throws Exception {
        runOnFxThread(() -> {
            enterCommand("sort rating squad");
            assertTrue(model.getFilteredPersonList().isEmpty());
            assertFalse(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            enterCommand("sort rating asc all");
            assertEquals(getTypicalPersons(), model.getFilteredPersonList());
        });
    }

    @Test
    public void execute_sortScopesThroughCommandBox_changesViewWithoutSaving() throws Exception {
        runOnFxThread(() -> {
            enterCommand("clear");
            enterCommand("add n/Amy p/123");
            enterCommand("add n/Bob p/456");
            enterCommand("rate 1 0");
            enterCommand("rate 2 10");
            enterCommand("keep 1");
            Path savedFile = testDirectory.resolve("players.json");
            byte[] originalFile = assertDoesNotThrow(() -> Files.readAllBytes(savedFile));
            enterCommand("squad");
            enterCommand("sort rating all");
            assertEquals("Bob", model.getFilteredPersonList().getFirst().getName().fullName);
            assertEquals(2, model.getFilteredPersonList().size());
            enterCommand("sort rating squad asc");
            assertEquals("Amy", model.getFilteredPersonList().getFirst().getName().fullName);
            assertEquals(1, model.getFilteredPersonList().size());
            enterCommand("sort rating squad up");
            assertTrue(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertEquals(1, model.getFilteredPersonList().size());
            enterCommand("list");
            assertEquals("Amy", model.getFilteredPersonList().getFirst().getName().fullName);
            assertArrayEquals(originalFile, assertDoesNotThrow(() -> Files.readAllBytes(savedFile)));
        });
    }

    @Test
    public void execute_filterThroughCommandBox_matchingErrorsAndHelp() throws Exception {
        runOnFxThread(() -> {
            enterCommand("add n/Tag Trialist p/123 t/Striker");
            enterCommand("help");
            assertHelpSummaryShown();
            enterCommand("filter t/STRIKER");
            assertPlayerPanelShown();
            enterCommand("keep 1");
            enterCommand("rate 1 10");
            assertEquals(SelectionStatus.KEPT, model.getFilteredPersonList().getFirst().getSelectionStatus());
            assertEquals("10", model.getFilteredPersonList().getFirst().getRating().toString());
            enterCommand("squad");
            enterCommand("filter t/STRIKER");
            assertEquals("1 player listed!", resultDisplay.getText());
            assertEquals("Tag Trialist", model.getFilteredPersonList().getFirst().getName().fullName);
            enterCommand("filter t/");
            assertEquals(Tag.MESSAGE_EMPTY, resultDisplay.getText());
            assertTrue(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertEquals(1, model.getFilteredPersonList().size());
            enterCommand("filter t/NoSuchTag");
            assertEquals("0 players listed!", resultDisplay.getText());
            assertTrue(model.getFilteredPersonList().isEmpty());
            enterCommand("filter t/Striker");
            enterCommand("delete 1");
            assertTrue(model.getFilteredPersonList().isEmpty());
            enterCommand("list");
            assertEquals(getTypicalPersons(), model.getFilteredPersonList());
        });
    }

    @Test
    public void execute_taggedPlayerThroughStatusAndRatingCommands_preservesAllFields() throws Exception {
        runOnFxThread(() -> {
            enterCommand("add n/Tag Trialist p/123 t/Striker");
            enterCommand("find Trialist");
            enterCommand("keep 1");
            enterCommand("rate 1 10");
            Person keptPlayer = model.getFilteredPersonList().getFirst();
            assertEquals(SelectionStatus.KEPT, keptPlayer.getSelectionStatus());
            assertEquals("10", keptPlayer.getRating().toString());
            assertTrue(keptPlayer.getTags().contains(new Tag("Striker")));
            enterCommand("squad");
            assertEquals(List.of(keptPlayer), model.getFilteredPersonList());

            enterCommand("rate 1 0");
            Person ratedPlayer = model.getFilteredPersonList().getFirst();
            assertEquals("0", ratedPlayer.getRating().toString());
            assertEquals(keptPlayer.getTags(), ratedPlayer.getTags());
            assertEquals(SelectionStatus.KEPT, ratedPlayer.getSelectionStatus());
            AddressBook original = new AddressBook(model.getAddressBook());
            for (String invalid : List.of("rate 1 11", "tag 1 t/Bad!", "keep 2")) {
                enterCommand(invalid);
                assertTrue(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
                assertEquals(original, model.getAddressBook());
            }
            enterCommand("cut 1");
            enterCommand("squad");
            assertTrue(model.getFilteredPersonList().isEmpty());
            assertPlayerPanelShown();
        });
    }

    @Test
    public void execute_tagThroughCommandBox_normalBoundaryAndErrors() throws Exception {
        runOnFxThread(() -> {
            enterCommand("add n/Tag Trialist p/123 t/Striker t/Trial Group 2");
            assertPlayerPanelShown();
            assertTrue(resultDisplay.getText().contains("Tags: Striker, Trial Group 2"));
            enterCommand("find Trialist");
            enterCommand("help");
            assertHelpSummaryShown();
            enterCommand("tag 1 t/" + "A".repeat(Tag.MAX_LENGTH));
            assertPlayerPanelShown();
            assertEquals("Updated tags for Tag Trialist: " + "A".repeat(Tag.MAX_LENGTH), resultDisplay.getText());
            AddressBook originalData = new AddressBook(model.getAddressBook());
            String tooLong = "A".repeat(Tag.MAX_LENGTH + 1);
            Map<String, String> expectedErrors = Map.of(
                    "tag 1 t/Bad!", String.format(Tag.MESSAGE_BAD_CHARACTER, "Bad!", "!"),
                    "tag 1 t/" + tooLong, String.format(Tag.MESSAGE_TOO_LONG, tooLong, Tag.MAX_LENGTH + 1),
                    "tag 1 t/ t/Striker", Tag.MESSAGE_EMPTY,
                    "edit 1 p/789 t/Captain", TagCommand.MESSAGE_USE_TAG);
            for (String input : expectedErrors.keySet()) {
                enterCommand(input);
                assertEquals(expectedErrors.get(input), resultDisplay.getText());
                assertTrue(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
                assertEquals(originalData, model.getAddressBook());
            }
            enterCommand("find Trialist");
            enterCommand("tag 1 t/");
            assertEquals("Tag Trialist: tags cleared.", resultDisplay.getText());
            assertPlayerPanelShown();
            // Tag updates keep the search, so only the updated player is still shown
            assertEquals(1, model.getFilteredPersonList().size());
            assertTrue(model.getFilteredPersonList().getLast().getTags().isEmpty());
            assertFalse(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        });
    }

    @BeforeEach
    public void setUp() throws Exception {
        runOnFxThread(() -> {
            model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
            Path dataFile = testDirectory.resolve("players.json");
            LogicManager logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(dataFile),
                    new JsonUserPrefsStorage(testDirectory.resolve("prefs.json"))));
            window = new MainWindow(new Stage(), logic, dataFile);
            window.fillInnerParts();
            commandField = (TextField) window.getRoot().getScene().lookup("#commandTextField");
            resultDisplay = (TextArea) window.getRoot().getScene().lookup("#resultDisplay");
            playerArea = (StackPane) window.getRoot().getScene().lookup("#personListPanelPlaceholder");
        });
    }

    @AfterEach
    public void tearDown() throws Exception {
        runOnFxThread(() -> {
            if (window != null) {
                window.getPrimaryStage().close();
            }
        });
    }

    @Test
    public void constructor_initialView_showsPlayersAndCouchCoachTitle() throws Exception {
        runOnFxThread(() -> {
            assertEquals("CouchCoach", window.getPrimaryStage().getTitle());
            assertPlayerPanelShown();
            assertEquals(getTypicalPersons(), model.getFilteredPersonList());
        });
    }

    @Test
    public void execute_helpAfterFind_preservesDataAndFilterWithoutSaving() throws Exception {
        runOnFxThread(() -> {
            enterCommand("find Alice");
            AddressBook originalData = new AddressBook(model.getAddressBook());
            enterCommand("help");

            assertHelpSummaryShown();
            assertEquals(HelpCommand.SHOWING_HELP_MESSAGE, resultDisplay.getText());
            assertEquals(List.of(ALICE), model.getFilteredPersonList());
            assertEquals(originalData, model.getAddressBook());
            assertFalse(Files.exists(testDirectory.resolve("players.json")));
            assertTrue(Window.getWindows().stream().noneMatch(Window::isShowing));
        });
    }

    @Test
    public void handleHelp_repeatedMenuRequests_reusesInlinePanel() throws Exception {
        runOnFxThread(() -> {
            MenuBar menuBar = (MenuBar) window.getRoot().getScene().lookup("#menuBar");
            menuBar.getMenus().get(1).getItems().get(0).fire();
            ScrollPane help = getDisplayedHelp();
            menuBar.getMenus().get(1).getItems().get(0).fire();

            assertSame(help, getDisplayedHelp());
            assertEquals(1, playerArea.getChildren().size());
        });
    }

    @Test
    public void helpShortcut_textInputTargets_showsInlineSummary() throws Exception {
        runOnFxThread(() -> {
            for (TextInputControl target : List.of(commandField, resultDisplay)) {
                enterCommand("list");
                target.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1,
                        false, false, false, false));
                assertHelpSummaryShown();
            }
        });
    }

    @Test
    public void execute_playerCommandsAfterHelp_restoresPlayerPanel() throws Exception {
        runOnFxThread(() -> {
            enterCommand("help");
            enterCommand("list");
            assertPlayerPanelShown();
            assertEquals(getTypicalPersons(), model.getFilteredPersonList());
            enterCommand("help");
            enterCommand("find Alice");
            assertPlayerPanelShown();
            assertEquals(List.of(ALICE), model.getFilteredPersonList());
            enterCommand("help");
            enterCommand("delete 1");
            assertPlayerPanelShown();
            assertTrue(model.getFilteredPersonList().isEmpty());
            assertFalse(model.hasPerson(ALICE));
            enterCommand("help");
            enterCommand("add n/Test Player p/91234567");
            assertPlayerPanelShown();
            assertEquals(getTypicalPersons().size(), model.getFilteredPersonList().size());
            assertEquals("Test Player", model.getFilteredPersonList().getLast().getName().fullName);
        });
    }

    @Test
    public void execute_invalidCommandsWhileHelpShown_retainsSummaryAndPlayerData() throws Exception {
        runOnFxThread(() -> {
            enterCommand("find Alice");
            enterCommand("help");
            ScrollPane help = getDisplayedHelp();
            AddressBook originalData = new AddressBook(model.getAddressBook());
            List<Person> originalList = List.copyOf(model.getFilteredPersonList());

            for (String input : List.of("", " \t ", "addd", "Add", "remove 2")) {
                enterCommand(input);
                assertEquals(Messages.MESSAGE_UNKNOWN_COMMAND, resultDisplay.getText());
                assertTrue(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
                assertSame(help, getDisplayedHelp());
            }
            enterCommand("delete 99");
            assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, resultDisplay.getText());
            assertSame(help, getDisplayedHelp());
            assertEquals(originalData, model.getAddressBook());
            assertEquals(originalList, model.getFilteredPersonList());
        });
    }

    @Test
    public void execute_paddedJerseysThroughCommandBox_normalizesWarnsAndRejectsInvalid() throws Exception {
        runOnFxThread(() -> {
            enterCommand("clear");
            enterCommand("add n/Amy p/123 j/099");
            assertEquals("99", model.getFilteredPersonList().getFirst().getJerseyNumber().orElseThrow().toString());
            enterCommand("add n/Bob p/456 j/99");
            assertTrue(resultDisplay.getText().contains("Warning: jersey number 99 is also used by Amy."));
            enterCommand("edit 2 j/010");
            assertEquals("10", model.getFilteredPersonList().getLast().getJerseyNumber().orElseThrow().toString());
            AddressBook savedTeam = new AddressBook(model.getAddressBook());
            enterCommand("edit 2 j/0100");
            assertEquals(JerseyNumber.MESSAGE_CONSTRAINTS, resultDisplay.getText());
            assertTrue(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertEquals(savedTeam, model.getAddressBook());
            enterCommand("edit 2 j/000");
            assertEquals("0", model.getFilteredPersonList().getLast().getJerseyNumber().orElseThrow().toString());
        });
    }

    @Test
    public void showFeedback_unreadableDataFileWarning_shownInResultDisplay() throws Exception {
        runOnFxThread(() -> {
            window.showFeedback(Messages.MESSAGE_DATA_FILE_UNREADABLE);
            assertEquals(Messages.MESSAGE_DATA_FILE_UNREADABLE, resultDisplay.getText());
        });
    }

    /**
     * Submits a command through the actual command-box event handler.
     */
    private void enterCommand(String command) {
        commandField.setText(command);
        commandField.fireEvent(new ActionEvent());
    }

    /**
     * Verifies that the CouchCoach help summary is displayed.
     */
    private void assertHelpSummaryShown() {
        Label heading = (Label) getDisplayedHelp().getContent().lookup("#helpHeading");
        assertEquals("CouchCoach commands:", heading.getText());
    }

    /**
     * Returns the help control displayed in the player-list area.
     */
    private ScrollPane getDisplayedHelp() {
        assertEquals(1, playerArea.getChildren().size());
        return (ScrollPane) playerArea.getChildren().getFirst();
    }

    /**
     * Verifies that the player-list area contains the original player panel.
     */
    private void assertPlayerPanelShown() {
        assertEquals(1, playerArea.getChildren().size());
        assertSame(window.getPersonListPanel().getRoot(), playerArea.getChildren().getFirst());
    }
}
