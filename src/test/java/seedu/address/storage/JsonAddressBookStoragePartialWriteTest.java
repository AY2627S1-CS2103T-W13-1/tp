package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.collections.ObservableList;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

@EnabledOnOs({OS.MAC, OS.LINUX})
public class JsonAddressBookStoragePartialWriteTest {
    @TempDir
    public Path testFolder;

    @Test
    public void saveAddressBook_partialJsonWrite_preservesLastSaveAndSupportsRetry() throws Exception {
        assertFailedSavePreservesFile(1);
    }

    @Test
    public void saveAddressBook_partialStagingCopy_preservesLastSaveAndSupportsRetry() throws Exception {
        assertFailedSavePreservesFile(100);
    }

    private void assertFailedSavePreservesFile(int playerCount) throws Exception {
        String players = java.util.stream.IntStream.range(0, playerCount)
                .mapToObj(index -> "{\"name\":\"Player " + index + "\",\"phone\":\"91234567\"}")
                .collect(Collectors.joining(","));
        String original = "{\"persons\":[" + players + "]}";
        Path data = Files.writeString(testFolder.resolve("players.json"), original);
        String classPath = Stream.of(getClass(), JsonAddressBookStorage.class, ObservableList.class,
                ObjectMapper.class, JsonProperty.class, JsonFactory.class, JavaTimeModule.class)
                .map(type -> Path.of(URI.create(type.getProtectionDomain().getCodeSource()
                        .getLocation().toString())).toString())
                .distinct().collect(Collectors.joining(File.pathSeparator));
        // Limit only the child JVM; failure must occur during a real write without endangering the test runner.
        Process child = new ProcessBuilder("sh", "-c", "trap '' XFSZ; ulimit -f 1; exec \"$@\"", "save-probe",
                Path.of(System.getProperty("java.home"), "bin", "java").toString(), "-cp", classPath,
                PartialWriteProcess.class.getName(), data.toString())
                .directory(testFolder.toFile()).redirectErrorStream(true).start();
        try {
            assertTrue(child.waitFor(30, TimeUnit.SECONDS), "Partial-write probe timed out");
            String output = new String(child.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, child.exitValue(), output);
            assertEquals(original, Files.readString(data));
            try (var files = Files.list(testFolder)) {
                assertFalse(files.anyMatch(path -> path.getFileName().toString().endsWith(".tmp")));
            }
        } finally {
            child.destroyForcibly();
        }
        JsonAddressBookStorage storage = new JsonAddressBookStorage(data);
        var restored = storage.readAddressBook().orElseThrow();
        assertEquals(playerCount, restored.getPersonList().size());
        storage.saveAddressBook(restored);
        assertEquals(restored, storage.readAddressBook().orElseThrow());
    }

    /** Entry point of the disposable child JVM used to trigger an actual partial-write IOException. */
    public static class PartialWriteProcess {
        public static void main(String[] args) throws Exception {
            Path data = Path.of(args[0]);
            JsonAddressBookStorage storage = new JsonAddressBookStorage(data);
            ModelManager model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
            LogicManager logic = new LogicManager(model, new StorageManager(storage,
                    new JsonUserPrefsStorage(data.resolveSibling("prefs.json"))));
            LogManager.getLogManager().reset();
            String feedback = logic.execute("remark 1 r/" + "x".repeat(20_000)).getFeedbackToUser();
            if (!feedback.contains(LogicManager.MESSAGE_SAVE_FAILURE)) {
                throw new AssertionError("Expected a real write failure");
            }
        }
    }
}
