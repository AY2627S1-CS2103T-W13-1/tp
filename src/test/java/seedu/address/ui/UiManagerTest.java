package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class UiManagerTest {
    @TempDir
    public Path testDirectory;

    private Stage stage;

    @AfterEach
    public void tearDown() throws Exception {
        runOnFxThread(() -> {
            if (stage != null) {
                stage.close();
            }
        });
    }

    @Test
    public void start_withStartupMessage_showsMessageInResultDisplay() throws Exception {
        assertEquals(Messages.MESSAGE_DATA_FILE_UNREADABLE, getResultDisplayTextAfterStart(
                Messages.MESSAGE_DATA_FILE_UNREADABLE));
    }

    @Test
    public void start_withoutStartupMessage_leavesResultDisplayEmpty() throws Exception {
        assertEquals("", getResultDisplayTextAfterStart(""));
    }

    /**
     * Starts the UI on a new stage with the given {@code startupMessage}
     * and returns the text shown in its result display.
     */
    private String getResultDisplayTextAfterStart(String startupMessage) throws Exception {
        String[] resultText = new String[1];
        runOnFxThread(() -> {
            Path dataFile = testDirectory.resolve("players.json");
            LogicManager logic = new LogicManager(new ModelManager(getTypicalAddressBook(), new UserPrefs()),
                    new StorageManager(new JsonAddressBookStorage(dataFile),
                            new JsonUserPrefsStorage(testDirectory.resolve("prefs.json"))));
            stage = new Stage();
            new UiManager(logic, dataFile, startupMessage).start(stage);
            resultText[0] = ((TextArea) stage.getScene().lookup("#resultDisplay")).getText();
        });
        return resultText[0];
    }
}
