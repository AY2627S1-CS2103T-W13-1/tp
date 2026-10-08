package seedu.address.ui;

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
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
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
