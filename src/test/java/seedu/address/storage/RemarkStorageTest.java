package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class RemarkStorageTest {
    @TempDir
    public Path tempDir;

    @Test
    public void saveAndRead_nonDefaultValue_preservesRemark() throws Exception {
        Person player = new PersonBuilder().withRemark("Strong left foot").build();
        AddressBook team = new AddressBook();
        team.addPerson(player);
        Path file = tempDir.resolve("players.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);

        storage.saveAddressBook(team);
        assertTrue(Files.readString(file).contains("\"remark\""));
        assertEquals(team, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void read_legacyData_defaultsToEmptyRemark() throws Exception {
        Path file = tempDir.resolve("legacy.json");
        Files.writeString(file, "{\"persons\":[{\"name\":\"Amy Bee\",\"phone\":\"85355255\"}]}");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        Person player = storage.readAddressBook().orElseThrow().getPersonList().getFirst();

        assertEquals(new PersonBuilder().build(), player);
        storage.saveAddressBook(storage.readAddressBook().orElseThrow());
        assertEquals(player, storage.readAddressBook().orElseThrow().getPersonList().getFirst());
    }
}
