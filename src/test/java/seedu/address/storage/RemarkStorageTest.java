package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;
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

    @Test
    public void saveAndReload_remarkRatingStatusAndTagEdits_preserveOtherFields() throws Exception {
        Person original = new PersonBuilder().withTags("Striker", "Captain")
                .withSelectionStatus(SelectionStatus.KEPT).withRating("8").withRemark("Strong left foot").build();
        AddressBook team = new AddressBook();
        team.addPerson(original);
        Model model = new ModelManager(team, new UserPrefs());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(tempDir.resolve("updated-players.json"));
        AddressBookParser parser = new AddressBookParser();
        Person remarked = new PersonBuilder(original).withRemark("Improved passing").build();
        Person rated = new PersonBuilder(remarked).withRating("9").build();
        Person cut = new PersonBuilder(rated).withSelectionStatus(SelectionStatus.CUT).build();
        Person edited = new PersonBuilder(cut).withName("Updated Player").withPhone("98765432")
                .withTags("Goalkeeper").build();
        List<String> commands = List.of("remark 1 r/Improved passing", "rate 1 9", "cut 1",
                "edit 1 n/Updated Player p/98765432 t/Goalkeeper", "remark 1 r/");
        List<Person> expectedPlayers = List.of(remarked, rated, cut, edited,
                new PersonBuilder(edited).withRemark("").build());

        for (int i = 0; i < commands.size(); i++) {
            parser.parseCommand(commands.get(i)).execute(model);
            storage.saveAddressBook(model.getAddressBook());
            model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
            assertEquals(expectedPlayers.get(i), model.getAddressBook().getPersonList().getFirst());
        }
    }
}
