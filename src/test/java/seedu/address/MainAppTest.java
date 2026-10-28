package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.util.SampleDataUtil;

public class MainAppTest {
    @TempDir
    public Path testDirectory;

    private MainApp app;
    private Path prefsFile;
    private Path dataFile;
    private Stage stage;

    @BeforeEach
    public void setUp() {
        prefsFile = testDirectory.resolve("preferences.json");
        dataFile = testDirectory.resolve("data").resolve("players.json");
        app = new MainApp();
        app.setFilePaths(prefsFile, dataFile);
    }

    @AfterEach
    public void tearDown() throws Exception {
        runOnFxThread(() -> {
            if (stage != null) {
                stage.close();
            }
        });
    }

    @Test
    public void initAndStart_noDataFile_showsSampleTeamWithoutWarning() throws Exception {
        app.init();

        assertEquals(SampleDataUtil.getSampleAddressBook(), app.model.getAddressBook());
        assertEquals("", startAndGetResultDisplayText());
    }

    @Test
    public void initAndStart_unreadableDataFile_showsWarningAndKeepsFile() throws Exception {
        Files.createDirectories(dataFile.getParent());
        byte[] corruptData = "{ \"persons\": [ { \"name\": ".getBytes();
        Files.write(dataFile, corruptData);

        app.init();

        assertEquals(new AddressBook(), app.model.getAddressBook());
        assertEquals(Messages.MESSAGE_DATA_FILE_UNREADABLE, startAndGetResultDisplayText());
        assertArrayEquals(corruptData, Files.readAllBytes(dataFile));
    }

    @Test
    public void initAndStart_nullPlayer_showsWarningAndKeepsFile() throws Exception {
        Files.createDirectories(dataFile.getParent());
        byte[] invalidData = "{\"persons\":[null]}".getBytes();
        Files.write(dataFile, invalidData);

        app.init();

        assertEquals(new AddressBook(), app.model.getAddressBook());
        assertEquals(Messages.MESSAGE_DATA_FILE_UNREADABLE, startAndGetResultDisplayText());
        assertArrayEquals(invalidData, Files.readAllBytes(dataFile));
    }

    @Test
    public void stop_afterInit_savesPreferences() throws Exception {
        app.init();
        Files.deleteIfExists(prefsFile);

        app.stop();

        assertTrue(Files.exists(prefsFile));
    }

    /**
     * Starts the app's UI on a new stage and returns the text shown in its result display.
     */
    private String startAndGetResultDisplayText() throws Exception {
        String[] resultText = new String[1];
        runOnFxThread(() -> {
            stage = new Stage();
            app.start(stage);
            resultText[0] = ((TextArea) stage.getScene().lookup("#resultDisplay")).getText();
        });
        return resultText[0];
    }
}
