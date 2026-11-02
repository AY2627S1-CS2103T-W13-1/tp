package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;

public class JsonAddressBookStorageSaveTest {
    @TempDir
    public Path testFolder;

    @Test
    public void saveAddressBook_missingParent_createsReloadableFileWithoutTemporaryFiles() throws Exception {
        Path data = testFolder.resolve("nested/players.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(data);
        AddressBook team = getTypicalAddressBook();
        storage.saveAddressBook(team);
        assertEquals(team, storage.readAddressBook().orElseThrow());
        try (var files = Files.list(data.getParent())) {
            assertEquals(java.util.List.of(data), files.toList());
        }
    }

    @Test
    @EnabledOnOs({OS.MAC, OS.LINUX})
    public void saveAddressBook_symbolicLink_updatesTargetAndRetainsLink() throws Exception {
        Path target = testFolder.resolve("target.json");
        Files.writeString(target, "{}");
        Path link = Files.createSymbolicLink(testFolder.resolve("players.json"), target.getFileName());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(link);
        AddressBook team = getTypicalAddressBook();
        storage.saveAddressBook(team);
        assertTrue(Files.isSymbolicLink(link));
        assertEquals(team, new JsonAddressBookStorage(target).readAddressBook().orElseThrow());
        assertEquals(team, storage.readAddressBook().orElseThrow());
    }

    @Test
    @EnabledOnOs({OS.MAC, OS.LINUX})
    public void saveAddressBook_existingPermissions_preservesPermissions() throws Exception {
        Path data = testFolder.resolve("players.json");
        Files.writeString(data, "{}");
        var permissions = PosixFilePermissions.fromString("rw-r-----");
        Files.setPosixFilePermissions(data, permissions);
        new JsonAddressBookStorage(data).saveAddressBook(getTypicalAddressBook());
        assertEquals(permissions, Files.getPosixFilePermissions(data));
    }

    @Test
    @EnabledOnOs({OS.MAC, OS.LINUX})
    public void saveAddressBook_readOnlyFile_rejectsSaveAndPreservesData() throws Exception {
        Path data = testFolder.resolve("players.json");
        Files.writeString(data, "{\"persons\":[]}");
        Files.setPosixFilePermissions(data, PosixFilePermissions.fromString("r--r--r--"));
        try {
            assertThrows(IOException.class, () -> new JsonAddressBookStorage(data)
                    .saveAddressBook(getTypicalAddressBook()));
            assertEquals("{\"persons\":[]}", Files.readString(data));
            try (var files = Files.list(testFolder)) {
                assertEquals(java.util.List.of(data), files.toList());
            }
        } finally {
            Files.setPosixFilePermissions(data, PosixFilePermissions.fromString("rw-------"));
        }
    }

    @Test
    public void saveAddressBook_directoryTarget_preservesContentsAndRemovesTemporaryFile() throws Exception {
        Path data = Files.createDirectory(testFolder.resolve("players.json"));
        Path sentinel = Files.writeString(data.resolve("keep.txt"), "existing data");
        assertThrows(IOException.class, () -> new JsonAddressBookStorage(data)
                .saveAddressBook(getTypicalAddressBook()));
        assertEquals("existing data", Files.readString(sentinel));
        try (var files = Files.list(testFolder)) {
            assertEquals(java.util.List.of(data), files.toList());
        }
    }
}
