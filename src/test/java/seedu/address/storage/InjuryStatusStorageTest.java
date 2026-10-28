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
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;
import seedu.address.testutil.PersonBuilder;

public class InjuryStatusStorageTest {
    @TempDir
    public Path tempDir;

    @Test
    public void saveAndRead_nonDefaultValue_preservesInjuryStatus() throws Exception {
        Person player = new PersonBuilder().withInjuryStatus(InjuryStatus.INJURED).build();
        AddressBook team = new AddressBook();
        team.addPerson(player);
        Path file = tempDir.resolve("players.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);

        storage.saveAddressBook(team);
        assertTrue(Files.readString(file).contains("\"injuryStatus\""));
        assertEquals(team, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void read_legacyData_defaultsToFit() throws Exception {
        Path file = tempDir.resolve("legacy.json");
        Files.writeString(file, "{\"persons\":[{\"name\":\"Amy Bee\",\"phone\":\"85355255\"}]}");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        Person player = storage.readAddressBook().orElseThrow().getPersonList().getFirst();

        assertEquals(new PersonBuilder().build(), player);
        storage.saveAddressBook(storage.readAddressBook().orElseThrow());
        assertEquals(player, storage.readAddressBook().orElseThrow().getPersonList().getFirst());
    }

    @Test
    public void saveAndReload_remarkInjuryAndEdits_preserveBothFieldsAfterEveryUpdate() throws Exception {
        Person original = new PersonBuilder().withTags("Striker", "Captain").withRating("8")
                .withSelectionStatus(SelectionStatus.KEPT).build();
        Person other = new PersonBuilder().withName("Other Player").withRemark("Leave unchanged").build();
        AddressBook team = new AddressBook();
        team.setPersons(List.of(original, other));
        Model model = new ModelManager(team, new UserPrefs());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(tempDir.resolve("combined-players.json"));
        AddressBookParser parser = new AddressBookParser();
        Person remarked = new PersonBuilder(original).withRemark("Strong left foot").build();
        Person injured = new PersonBuilder(remarked).withInjuryStatus(InjuryStatus.INJURED).build();
        Person replaced = new PersonBuilder(injured).withRemark("Improved passing").build();
        Person tagged = new PersonBuilder(replaced).withTags("Goalkeeper").build();
        Person edited = new PersonBuilder(tagged).withName("Updated Player").withPhone("98765432")
                .withTags("Goalkeeper").build();
        Person fit = new PersonBuilder(edited).withInjuryStatus(InjuryStatus.FIT).build();
        Person injuredAgain = new PersonBuilder(fit).withInjuryStatus(InjuryStatus.INJURED).build();
        Person cleared = new PersonBuilder(injuredAgain).withRemark("").build();
        Person editedAgain = new PersonBuilder(cleared).withPhone("91234567").build();
        List<String> commands = List.of("remark 1 r/Strong left foot", "injury 1 i/injured",
                "remark 1 r/Improved passing", "tag 1 t/Goalkeeper", "edit 1 n/Updated Player p/98765432",
                "injury 1 i/fit", "injury 1 i/injured", "remark 1 r/", "edit 1 p/91234567");
        List<Person> expectedPlayers = List.of(remarked, injured, replaced, tagged, edited, fit, injuredAgain,
                cleared, editedAgain);

        for (int i = 0; i < commands.size(); i++) {
            parser.parseCommand(commands.get(i)).execute(model);
            assertEquals(List.of(expectedPlayers.get(i), other), model.getAddressBook().getPersonList());
            storage.saveAddressBook(model.getAddressBook());
            model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
            assertEquals(List.of(expectedPlayers.get(i), other), model.getAddressBook().getPersonList());
        }
    }

    @Test
    public void read_remarkDataWithoutInjuryStatus_defaultsToFitAndPreservesFields() throws Exception {
        Path file = tempDir.resolve("before-injury-status.json");
        Files.writeString(file, """
                {"persons":[{"name":"Amy Bee","phone":"85355255","selectionStatus":"KEPT",
                "rating":"8","tags":["Striker"],"remark":"Strong left foot"}]}
                """);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        Person expected = new PersonBuilder().withSelectionStatus(SelectionStatus.KEPT).withRating("8")
                .withTags("Striker").withRemark("Strong left foot").build();
        AddressBook team = new AddressBook(storage.readAddressBook().orElseThrow());
        assertEquals(expected, team.getPersonList().getFirst());
        storage.saveAddressBook(team);
        assertEquals(team, storage.readAddressBook().orElseThrow());
    }
}
