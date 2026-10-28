package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.FileHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LogsCenterTest {
    @TempDir
    public Path tempDir;

    @Test
    public void createFileHandler_unwritableLocation_returnsEmpty() {
        String logFileInMissingFolder = tempDir.resolve("missing").resolve("addressbook.log").toString();
        assertTrue(LogsCenter.createFileHandler(logFileInMissingFolder).isEmpty());
    }

    @Test
    public void createFileHandler_writableLocation_returnsHandler() {
        Optional<FileHandler> fileHandler = LogsCenter.createFileHandler(tempDir.resolve("test.log").toString());
        assertTrue(fileHandler.isPresent());
        fileHandler.get().close();
    }
}
