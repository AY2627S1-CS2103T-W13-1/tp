package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SaveCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @Test
    public void execute_clearTagsAfterFilter_reportsRemainingMatchesAndSavesChanges() throws Exception {
        logic.execute("add n/Amy p/111 t/Trial");
        logic.execute("add n/Bob p/222 t/Trial");
        logic.execute("add n/Charlie p/333 t/Trial");
        logic.execute("add n/Dana p/444");
        logic.execute("filter t/Trial i/fit");
        logic.execute("sort rating");
        assertThrows(ParseException.class, () -> logic.execute("tag 1 t/ t/Trial"));
        List<String> names = List.of("Amy", "Bob", "Charlie");
        List<String> summaries = List.of("2 players match", "1 player matches", "0 players match");
        for (int i = 0; i < names.size(); i++) {
            assertEquals(names.get(i) + ": tags cleared.\n" + summaries.get(i)
                    + " the current command: filter t/Trial i/fit",
                    logic.execute("tag 1 t/").getFeedbackToUser());
            assertEquals(2 - i, model.getFilteredPersonList().size());
        }
        assertThrows(CommandException.class, () -> logic.execute("tag 1 t/"));
        assertEquals(4, model.getAddressBook().getPersonList().size());
        assertTrue(model.getAddressBook().getPersonList().stream().allMatch(player -> player.getTags().isEmpty()));
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow());
    }

    @Test
    public void execute_pendingAndCutAfterSquad_reportsRemainingKeptPlayersAndSavesChanges() throws Exception {
        logic.execute("add n/Amy p/111");
        logic.execute("add n/Bob p/222");
        logic.execute("add n/Charlie p/333");
        logic.execute("add n/Dana p/444");
        for (int i = 1; i <= 3; i++) {
            logic.execute("keep " + i);
        }
        logic.execute("squad");
        assertEquals("Amy is already marked as kept.", logic.execute("keep 1").getFeedbackToUser());
        assertThrows(CommandException.class, () -> logic.execute("cut 4"));
        assertEquals("Marked Amy as pending.\n2 players in your squad",
                logic.execute("pending 1").getFeedbackToUser());
        assertEquals("Marked Bob as cut.\n1 player in your squad", logic.execute("cut 1").getFeedbackToUser());
        assertEquals("Marked Charlie as pending.\n0 players in your squad",
                logic.execute("pending 1").getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertThrows(CommandException.class, () -> logic.execute("pending 1"));
        assertEquals(List.of(SelectionStatus.PENDING, SelectionStatus.CUT, SelectionStatus.PENDING,
                SelectionStatus.PENDING), model.getAddressBook().getPersonList().stream()
                .map(Person::getSelectionStatus).toList());
        assertEquals(model.getAddressBook(), new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow());
    }

    @Test
    public void execute_tagAndStatusWithFailedSave_reportsUpdatedCountsBeforeSaveWarning() throws Exception {
        logic.execute("add n/Amy p/111 t/Trial");
        logic.execute("keep 1");
        logic.execute("filter t/Trial");
        logic = createLogicWithFailingSave();
        assertEquals("Amy: tags cleared.\n0 players match the current command: filter t/Trial\n"
                + LogicManager.MESSAGE_SAVE_FAILURE, logic.execute("tag 1 t/").getFeedbackToUser());
        logic.execute("squad");
        assertEquals("Marked Amy as cut.\n0 players in your squad\n" + LogicManager.MESSAGE_SAVE_FAILURE,
                logic.execute("cut 1").getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_editAndDeleteAfterFind_reportsUpdatedCountsAndRetainsCommand() throws Exception {
        logic.execute("add n/Amy One p/111");
        logic.execute("add n/Amy Two p/222");
        assertEquals("2 players match the current command: find Amy",
                logic.execute("find Amy").getFeedbackToUser());
        assertEquals("Edited player: Bob; Phone: 111\n1 player matches the current command: find Amy",
                logic.execute("edit 1 n/Bob").getFeedbackToUser());
        assertEquals("Deleted player: Amy Two; Phone: 222\n0 players match the current command: find Amy",
                logic.execute("delete 1").getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_editAndDeleteAfterFilter_preservesConditionsAndUsesUpdatedCount() throws Exception {
        logic.execute("add n/Amy p/111 t/Trial Group 2");
        logic.execute("add n/Bob p/222 t/Trial Group 2");
        logic.execute("add n/Charlie p/333");
        String command = "filter i/FIT t/Trial Group 2";
        String summary = "2 players match the current command: " + command;
        assertEquals(summary, logic.execute("  filter  i/FIT\tt/Trial   Group 2 \u00a0").getFeedbackToUser());
        assertEquals("Edited player: Amy; Phone: 444; Tags: Trial Group 2\n" + summary,
                logic.execute("edit 1 p/444").getFeedbackToUser());
        assertEquals("Deleted player: Bob; Phone: 222; Tags: Trial Group 2\n"
                + "1 player matches the current command: " + command,
                logic.execute("delete 2").getFeedbackToUser());
        assertEquals("Deleted player: Amy; Phone: 444; Tags: Trial Group 2\n"
                + "0 players match the current command: " + command,
                logic.execute("delete 1").getFeedbackToUser());
        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_editAndDeleteAfterSquad_reportsSquadCountIncludingZero() throws Exception {
        logic.execute("add n/Amy p/111");
        logic.execute("add n/Bob p/222");
        logic.execute("keep 1");
        logic.execute("keep 2");
        logic.execute("squad");
        assertEquals("Edited player: Amy; Phone: 333\n2 players in your squad",
                logic.execute("edit 1 p/333").getFeedbackToUser());
        assertEquals("Deleted player: Amy; Phone: 333\n1 player in your squad",
                logic.execute("delete 1").getFeedbackToUser());
        assertEquals("Deleted player: Bob; Phone: 222\n0 players in your squad",
                logic.execute("delete 1").getFeedbackToUser());
    }

    @Test
    public void execute_viewChangesAndErrors_keepSummaryConsistentWithDisplayedView() throws Exception {
        logic.execute("add n/Amy p/111 t/Trial");
        logic.execute("add n/Bob p/222 t/Trial");
        logic.execute("filter t/Trial");
        logic.execute("find Amy");
        logic.execute("sort rating");
        logic.execute("help");
        assertThrows(ParseException.class, () -> logic.execute("filter t/"));
        assertThrows(CommandException.class, () -> logic.execute("delete 2"));
        assertThrows(CommandException.class, () -> logic.execute("edit 1 n/Bob"));
        assertEquals("No changes made: Amy already has these details.",
                logic.execute("edit 1 p/111").getFeedbackToUser());
        assertEquals("Edited player: Amy; Phone: 999; Tags: Trial\n"
                + "1 player matches the current command: find Amy",
                logic.execute("edit 1 p/999").getFeedbackToUser());
        logic.execute("list");
        assertEquals("Edited player: Amy; Phone: 333; Tags: Trial",
                logic.execute("edit 1 p/333").getFeedbackToUser());
        logic.execute("find Amy");
        logic.execute("add n/Charlie p/444");
        assertEquals("Deleted player: Charlie; Phone: 444", logic.execute("delete 3").getFeedbackToUser());
        logic.execute("find Amy");
        logic.execute("sort rating all");
        assertEquals("Edited player: Bob; Phone: 555; Tags: Trial",
                logic.execute("edit 2 p/555").getFeedbackToUser());
        logic.execute("keep 1");
        logic.execute("sort rating squad");
        assertEquals("Edited player: Amy; Phone: 666; Tags: Trial\n1 player in your squad",
                logic.execute("edit 1 p/666").getFeedbackToUser());
    }

    @Test
    public void execute_editWithWarningsAndSaveFailure_retainsFeedbackAndRefreshedSummary() throws Exception {
        logic.execute("add n/Amy p/111 j/7");
        logic.execute("add n/Bob p/222 j/8");
        logic.execute("find Amy");
        logic = createLogicWithFailingSave();
        assertEquals("Edited player: Amy; Phone: 111; Jersey: 8\n"
                + "Warning: jersey number 8 is also used by Bob.\n"
                + "1 player matches the current command: find Amy\n" + LogicManager.MESSAGE_SAVE_FAILURE,
                logic.execute("edit 1 j/8").getFeedbackToUser());
        assertEquals("Deleted player: Amy; Phone: 111; Jersey: 8\n"
                + "0 players match the current command: find Amy\n" + LogicManager.MESSAGE_SAVE_FAILURE,
                logic.execute("delete 1").getFeedbackToUser());
    }

    @Test
    public void execute_filter_doesNotSaveAndPreservesPreviousViewOnErrors() throws Exception {
        Person striker = new PersonBuilder().withName("Amy").withTags("Striker", "Trial Group 2").build();
        Person captain = new PersonBuilder().withName("Bob").withTags("Captain").build();
        model.addPerson(striker);
        model.addPerson(captain);
        logic = createLogicWithFailingSave();
        AddressBook original = new AddressBook(model.getAddressBook());
        logic.execute("find Bob");
        assertEquals("1 player matches the current command: filter t/striker",
                logic.execute("filter t/striker").getFeedbackToUser());
        assertEquals(List.of(striker), model.getFilteredPersonList());
        for (String input : List.of("filter", "filter Striker", "filter t/", "filter t/Bad!", "Filter t/Striker")) {
            assertThrows(ParseException.class, () -> logic.execute(input));
            assertEquals(List.of(striker), model.getFilteredPersonList());
        }
        assertEquals("2 players match the current command: filter t/Captain t/Trial Group 2",
                logic.execute("filter t/Captain t/Trial Group 2").getFeedbackToUser());
        assertEquals(List.of(striker, captain), model.getFilteredPersonList());
        assertEquals(original, model.getAddressBook());
        assertEquals("1 player matches the current command: filter t/Trial Group 2 t/trial group 2",
                logic.execute("filter t/Trial Group 2 t/trial group 2").getFeedbackToUser());
    }

    @Test
    public void execute_editKeepsFilterAndAddRestoresFullRoster_persistTags() throws Exception {
        logic.execute("add n/Amy p/123 t/Striker");
        logic.execute("add n/Bob p/456 t/Goalkeeper");
        logic.execute("filter t/Goalkeeper");
        logic.execute("edit 1 p/4567");
        assertEquals(List.of("Bob"), model.getFilteredPersonList().stream().map(p -> p.getName().fullName).toList());

        logic.execute("tag 1 t/Captain");
        assertEquals(List.of("Bob"), model.getFilteredPersonList().stream().map(p -> p.getName().fullName).toList());
        assertEquals(Set.of(new Tag("Captain"), new Tag("Goalkeeper")),
                model.getAddressBook().getPersonList().getLast().getTags());
        logic.execute("filter t/Striker");
        logic.execute("add n/Charlie p/789 t/Captain");
        assertEquals(3, model.getFilteredPersonList().size());
        JsonAddressBookStorage saved = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(model.getAddressBook(), saved.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_addAndTag_saveAppendPreserveAndClear() throws Exception {
        logic.execute("add n/Tagged Player p/123 t/Striker t/Trial Group 2 t/striker");
        Person expected = new PersonBuilder().withName("Tagged Player").withPhone("123")
                .withTags("Striker", "Trial Group 2").build();
        assertEquals(expected, model.getFilteredPersonList().getFirst());
        JsonAddressBookStorage saved = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(model.getAddressBook(), saved.readAddressBook().orElseThrow());

        logic.execute("edit 1 p/456");
        assertEquals(expected.getTags(), model.getFilteredPersonList().getFirst().getTags());
        logic.execute("tag 1 t/Goalkeeper");
        assertEquals(Set.of(new Tag("Goalkeeper"), new Tag("Striker"), new Tag("Trial Group 2")),
                model.getFilteredPersonList().getFirst().getTags());
        assertEquals(model.getAddressBook(), saved.readAddressBook().orElseThrow());
        logic.execute("tag 1 t/");
        assertTrue(model.getFilteredPersonList().getFirst().getTags().isEmpty());
        assertEquals(model.getAddressBook(), saved.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_tagSortedFilteredPlayer_appendsToDisplayedTargetAndRetainsView() throws Exception {
        logic.execute("add n/Amy p/111 t/Captain");
        logic.execute("add n/Bob p/222 t/Goalkeeper");
        logic.execute("add n/Charlie p/333 t/Captain");
        logic.execute("rate 1 2");
        logic.execute("rate 3 9");
        logic.execute("sort rating");
        logic.execute("filter t/Captain");

        logic.execute("tag 1 t/Goalkeeper t/captain");

        assertEquals(List.of("Charlie", "Amy"),
                model.getFilteredPersonList().stream().map(p -> p.getName().fullName).toList());
        assertEquals(Set.of("Captain", "Goalkeeper"), model.getFilteredPersonList().getFirst().getTags().stream()
                .map(Tag::toString).collect(Collectors.toSet()));
        assertEquals(Set.of(new Tag("Captain")), model.getAddressBook().getPersonList().getFirst().getTags());
        JsonAddressBookStorage saved = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(model.getAddressBook(), saved.readAddressBook().orElseThrow());

        logic.execute("tag 1 t/");
        assertEquals(List.of("Amy"), model.getFilteredPersonList().stream().map(p -> p.getName().fullName).toList());
        assertEquals(model.getAddressBook(), saved.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_invalidTags_preserveDataAndFile() throws Exception {
        logic.execute("add n/Tagged Player p/123 t/Striker");
        AddressBook original = new AddressBook(model.getAddressBook());
        logic = createLogicWithFailingSave();
        for (String command : List.of("add n/Other p/456 t/", "tag 1 t/ t/Striker", "tag 1 t/Striker t/",
                "tag 1 t/ t/")) {
            assertThrows(ParseException.class, Tag.MESSAGE_EMPTY, () -> logic.execute(command));
            assertEquals(original, model.getAddressBook());
        }
        assertThrows(ParseException.class, String.format(Tag.MESSAGE_BAD_CHARACTER, "Bad!", "!"), () ->
                logic.execute("tag 1 t/Bad!"));
        assertEquals(original, model.getAddressBook());
        assertThrows(CommandException.class,
                AddCommand.MESSAGE_DUPLICATE_PERSON, () -> logic.execute("add n/Tagged Player p/999 t/Goalkeeper"));
        assertThrows(CommandException.class,
                MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () -> logic.execute("tag 2 t/Striker"));
    }

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_clearWithArguments_preservesTeamViewAndSavedFile() throws Exception {
        logic.execute("add n/Amy p/123");
        logic.execute("add n/Bob p/456");
        logic.execute("find Amy");
        AddressBook original = new AddressBook(model.getAddressBook());
        List<Person> originalView = List.copyOf(model.getFilteredPersonList());
        Path savedFile = temporaryFolder.resolve("addressBook.json");
        byte[] originalFile = Files.readAllBytes(savedFile);

        for (String input : List.of("clear 1", "clear everything", "clear n/Amy", "clear /")) {
            assertThrows(ParseException.class, () -> logic.execute(input));
            assertEquals(original, model.getAddressBook());
            assertEquals(originalView, model.getFilteredPersonList());
            assertArrayEquals(originalFile, Files.readAllBytes(savedFile));
        }

        logic.execute("  clear \t ");
        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(new AddressBook(), new JsonAddressBookStorage(savedFile).readAddressBook().orElseThrow());
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_ratingAndStatusChanges_preserveBothFieldsOnReload() throws Exception {
        model.addPerson(new PersonBuilder().withTags("Striker").build());
        logic.execute("keep 1");
        logic.execute("rate 1 8");
        logic.execute("edit 1 p/12345678");
        JsonAddressBookStorage saved = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        Person kept = saved.readAddressBook().orElseThrow().getPersonList().get(0);
        assertEquals("8", kept.getRating().toString());
        assertEquals("Kept", kept.getSelectionStatus().toString());
        assertEquals(Set.of(new Tag("Striker")), kept.getTags());
        logic.execute("cut 1");
        Person cut = saved.readAddressBook().orElseThrow().getPersonList().get(0);
        assertEquals("8", cut.getRating().toString());
        assertEquals("Cut", cut.getSelectionStatus().toString());
        assertEquals(Set.of(new Tag("Striker")), cut.getTags());
        logic.execute("pending 1");
        Person pending = saved.readAddressBook().orElseThrow().getPersonList().get(0);
        assertEquals("8", pending.getRating().toString());
        assertEquals("Pending", pending.getSelectionStatus().toString());
        assertEquals(Set.of(new Tag("Striker")), pending.getTags());
    }

    @Test
    public void execute_tagEdits_preserveSelectionAndRatingOnReload() throws Exception {
        PersonBuilder expected = new PersonBuilder().withTags("Striker")
                .withSelectionStatus(SelectionStatus.KEPT).withRating("10");
        model.addPerson(expected.build());
        JsonAddressBookStorage saved = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));

        logic.execute("edit 1 n/Tag Captain p/456");
        assertEquals(expected.withName("Tag Captain").withPhone("456").build(),
                saved.readAddressBook().orElseThrow().getPersonList().getFirst());
        logic.execute("tag 1 t/Captain");
        assertEquals(expected.withTags("Captain", "Striker").build(),
                saved.readAddressBook().orElseThrow().getPersonList().getFirst());
        logic.execute("tag 1 t/");
        assertEquals(expected.withTags().build(),
                saved.readAddressBook().orElseThrow().getPersonList().getFirst());
        assertEquals(model.getAddressBook(), saved.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_sort_doesNotSaveAndListRestoresOrder() throws Exception {
        Person low = new PersonBuilder().withName("Low Player").withRating("0").build();
        Person high = new PersonBuilder().withName("High Player").withRating("10").build();
        model.addPerson(low);
        model.addPerson(high);
        logic = createLogicWithFailingSave();
        logic.execute("sort rating");
        assertEquals(List.of(high, low), logic.getFilteredPersonList());
        assertEquals(List.of(low, high), model.getAddressBook().getPersonList());
        logic.execute("list");
        assertEquals(List.of(low, high), logic.getFilteredPersonList());
    }

    @Test
    public void execute_rateThenReload_preservesRating() throws Exception {
        model.addPerson(new PersonBuilder().build());
        logic.execute("rate 1 8");
        JsonAddressBookStorage saved = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(model.getAddressBook(), saved.readAddressBook().orElseThrow());
        logic.execute("edit 1 p/12345678");
        assertEquals("8", saved.readAddressBook().orElseThrow().getPersonList().get(0).getRating().toString());
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_listAfterFind_restoresInsertionOrderWithoutSaving() throws Exception {
        List<Person> players = TypicalPersons.getTypicalPersons();
        players.forEach(model::addPerson);
        logic.execute("find Alice");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("list.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) {
                throw new AssertionError("Listing players must not save player data");
            }
        };
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("listPrefs.json"))));
        AddressBook original = new AddressBook(model.getAddressBook());

        assertEquals(1, logic.getFilteredPersonList().size());
        assertEquals("Listed all players", logic.execute("list").getFeedbackToUser());
        assertEquals(players, logic.getFilteredPersonList());

        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("NobodyMatchesThisName")));
        assertEquals(0, logic.getFilteredPersonList().size());
        assertEquals("Listed all players", logic.execute("  list all  ").getFeedbackToUser());
        assertEquals(players, logic.getFilteredPersonList());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_listEmptyTeam_success() throws Exception {
        assertEquals("Listed all players", logic.execute("list").getFeedbackToUser());
        assertEquals(List.of(), logic.getFilteredPersonList());
    }

    @Test
    public void execute_help_preservesPlayersAndFilterWithoutSaving() throws Exception {
        TypicalPersons.getTypicalPersons().forEach(model::addPerson);
        logic = createLogicWithFailingSave();
        logic.execute("find Alice");
        List<Person> previousList = List.copyOf(logic.getFilteredPersonList());
        AddressBook previousData = new AddressBook(model.getAddressBook());

        for (String input : List.of("help", "  help  ", "help extra")) {
            CommandResult result = logic.execute(input);
            assertEquals(HelpCommand.SHOWING_HELP_MESSAGE, result.getFeedbackToUser());
            assertTrue(result.isShowHelp());
            assertFalse(result.isExit());
            assertEquals(previousList, logic.getFilteredPersonList());
            assertEquals(previousData, model.getAddressBook());
        }
    }

    @Test
    public void execute_helpEmptyTeam_successWithoutSaving() throws Exception {
        logic = createLogicWithFailingSave();
        assertTrue(logic.execute("help").isShowHelp());
        assertEquals(List.of(), logic.getFilteredPersonList());
    }

    @Test
    public void execute_squad_showsKeptPlayersWithoutSaving() throws Exception {
        TypicalPersons.getTypicalPersons().forEach(model::addPerson);
        Person keptAlice = TypicalPersons.ALICE.withSelectionStatus(SelectionStatus.KEPT);
        model.setPerson(TypicalPersons.ALICE, keptAlice);
        // Any save attempt would throw, so this passes only if squad does not write the data file
        logic = createLogicWithFailingSave();
        AddressBook previousData = new AddressBook(model.getAddressBook());

        assertEquals("1 player in your squad.", logic.execute("squad").getFeedbackToUser());
        assertEquals(List.of(keptAlice), logic.getFilteredPersonList());
        assertEquals(previousData, model.getAddressBook());

        assertEquals("Listed all players", logic.execute("list").getFeedbackToUser());
        assertEquals(TypicalPersons.getTypicalPersons().size(), logic.getFilteredPersonList().size());
    }

    @Test
    public void execute_exit_successWithoutSaving() throws Exception {
        // Any save attempt would throw, so this passes only if exit does not write the data file
        logic = createLogicWithFailingSave();
        assertTrue(logic.execute("exit").isExit());
    }

    @Test
    public void execute_unrecognisedInput_preservesPlayersAndFilter() throws Exception {
        TypicalPersons.getTypicalPersons().forEach(model::addPerson);
        logic = createLogicWithFailingSave();
        logic.execute("find Alice");
        List<Person> previousList = List.copyOf(logic.getFilteredPersonList());
        AddressBook previousData = new AddressBook(model.getAddressBook());
        String expectedMessage = "Unknown command. Type 'help' to see the list of commands.";

        for (String input : List.of("", " \t ", "addd", "Add", "remove 2")) {
            assertThrows(ParseException.class, expectedMessage, () -> logic.execute(input));
            assertEquals(previousList, logic.getFilteredPersonList());
            assertEquals(previousData, model.getAddressBook());
        }
    }

    @Test
    public void execute_keep_savesStatusToDataFile() throws Exception {
        model.addPerson(AMY);
        assertEquals("Marked " + AMY.getName() + " as kept.", logic.execute("keep 1").getFeedbackToUser());

        // reading the saved file back shows the player as kept, so the decision survives a restart
        ReadOnlyAddressBook savedData = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow();
        assertEquals(SelectionStatus.KEPT, savedData.getPersonList().get(0).getSelectionStatus());
    }

    @Test
    public void execute_cutWhenSaveFails_returnsAppliedChangeWarning() throws Exception {
        model.addPerson(AMY);
        logic = createLogicWithFailingSave();
        assertTrue(logic.execute("cut 1").getFeedbackToUser().endsWith("\n" + LogicManager.MESSAGE_SAVE_FAILURE));
        assertEquals(SelectionStatus.CUT, model.getAddressBook().getPersonList().getFirst().getSelectionStatus());
    }

    @Test
    public void execute_storageThrowsIoException_returnsAppliedChangeWarning() throws Exception {
        assertSaveWarningForExceptionFromStorage(DUMMY_IO_EXCEPTION, LogicManager.MESSAGE_SAVE_FAILURE);
    }

    @Test
    public void execute_storageThrowsAdException_returnsAppliedChangeWarning() throws Exception {
        assertSaveWarningForExceptionFromStorage(DUMMY_AD_EXCEPTION, LogicManager.MESSAGE_SAVE_FAILURE);
    }

    @Test
    public void execute_deleteWithFailedSave_retrySavePreservesRemainingPlayerAndView() throws Exception {
        Person bob = new PersonBuilder().withName("Bob").withRating("9").withTags("Striker").build();
        Person charlie = new PersonBuilder().withName("Charlie").withRating("2").withTags("Striker").build();
        Person hiddenPlayer = new PersonBuilder().withName("Dan").withTags("Goalkeeper").build();
        model.addPerson(AMY);
        model.addPerson(charlie);
        model.addPerson(bob);
        model.addPerson(hiddenPlayer);
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        Files.createDirectory(dataFile);
        assertTrue(logic.execute("delete 1").getFeedbackToUser().endsWith("\n" + LogicManager.MESSAGE_SAVE_FAILURE));
        assertEquals(List.of(charlie, bob, hiddenPlayer), model.getAddressBook().getPersonList());
        logic.execute("sort rating");
        logic.execute("filter t/Striker");
        AddressBook remainingTeam = new AddressBook(model.getAddressBook());

        assertEquals(SaveCommand.MESSAGE_FAILURE, logic.execute("save").getFeedbackToUser());
        assertEquals(SaveCommand.MESSAGE_FAILURE, logic.execute("save").getFeedbackToUser());
        assertEquals(remainingTeam, model.getAddressBook());
        assertEquals(List.of(bob, charlie), model.getFilteredPersonList());

        Files.delete(dataFile);
        assertEquals(SaveCommand.MESSAGE_SUCCESS, logic.execute("save").getFeedbackToUser());
        assertEquals(remainingTeam, new JsonAddressBookStorage(dataFile).readAddressBook().orElseThrow());
        assertEquals(List.of(bob, charlie), model.getFilteredPersonList());
        assertEquals(remainingTeam, model.getAddressBook());
    }

    @Test
    public void execute_saveEmptyTeam_writesEmptyRoster() throws Exception {
        assertEquals(SaveCommand.MESSAGE_SUCCESS, logic.execute("save").getFeedbackToUser());
        assertEquals(new AddressBook(), new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow());
    }

    @Test
    public void execute_find_doesNotSaveOrChangePlayerData() throws Exception {
        Person tan = new PersonBuilder().withName("Tan Wei Ming").build();
        Person lim = new PersonBuilder().withName("Lim Wei Jie").build();
        Person nur = new PersonBuilder().withName("Nur'ain Binte Hassan").build();
        Person muthu = new PersonBuilder().withName("Muthu s/o Ramasamy").build();
        model.addPerson(tan);
        model.addPerson(lim);
        model.addPerson(nur);
        model.addPerson(muthu);
        logic = createLogicWithFailingSave();

        assertFindSuccess("find wei", "2 players match the current command: find wei", List.of(tan, lim));
        assertFindSuccess("find TAN", "1 player matches the current command: find TAN", List.of(tan));
        assertFindSuccess("find wei nur'ain", "3 players match the current command: find wei nur'ain",
                List.of(tan, lim, nur));
        assertFindSuccess("find ming tan ming", "1 player matches the current command: find ming tan ming",
                List.of(tan));
        assertFindSuccess("find We", "0 players match the current command: find We", List.of());
        assertFindSuccess("find " + tan.getPhone(),
                "0 players match the current command: find " + tan.getPhone(), List.of());
        assertFindSuccess("find S/O", "1 player matches the current command: find S/O", List.of(muthu));
        assertFindSuccess("  find  wei \t ming  ", "2 players match the current command: find wei ming",
                List.of(tan, lim));
    }

    @Test
    public void execute_findWithoutKeywords_preservesFilteredListAndDoesNotSave() throws Exception {
        model.addPerson(new PersonBuilder().withName("Tan Wei Ming").build());
        model.addPerson(new PersonBuilder().withName("Lim Wei Jie").build());
        logic = createLogicWithFailingSave();
        logic.execute("find tan");
        List<Person> previousList = List.copyOf(model.getFilteredPersonList());
        AddressBook previousData = new AddressBook(model.getAddressBook());
        String expectedMessage = "Invalid command format!\n"
                + "find: Finds players whose names contain any of the given keywords.\n"
                + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
                + "Example: find wei ming";

        assertThrows(ParseException.class, expectedMessage, () -> logic.execute("find"));
        assertThrows(ParseException.class, expectedMessage, () -> logic.execute("find \t "));
        assertEquals(previousList, model.getFilteredPersonList());
        assertEquals(previousData, model.getAddressBook());
    }

    @Test
    public void execute_findWithCapitalizedCommandWord_throwsParseException() {
        assertParseException("Find wei", MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Verifies a search's feedback, displayed players, and unchanged team data.
     */
    private void assertFindSuccess(String input, String expectedFeedback, List<Person> expectedPlayers)
            throws Exception {
        AddressBook previousData = new AddressBook(model.getAddressBook());
        assertEquals(expectedFeedback, logic.execute(input).getFeedbackToUser());
        assertEquals(expectedPlayers, model.getFilteredPersonList());
        assertEquals(previousData, model.getAddressBook());
    }

    /**
     * Creates logic whose storage fails if a command attempts to save player data.
     */
    private Logic createLogicWithFailingSave() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("noSave.json")) {
                    @Override
                    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                        throw DUMMY_IO_EXCEPTION;
                    }
                };
        JsonUserPrefsStorage prefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("noSavePrefs.json"));
        return new LogicManager(model, new StorageManager(addressBookStorage, prefsStorage));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the save warning expected in the command result
     */
    private void assertSaveWarningForExceptionFromStorage(IOException e, String expectedMessage) throws Exception {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        CommandResult result = logic.execute(addCommand);
        String appliedFeedback = String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(AMY));
        assertTrue(result.getFeedbackToUser().startsWith(appliedFeedback));
        assertTrue(result.getFeedbackToUser().endsWith("\n" + expectedMessage));
        assertEquals(expectedModel, model);
    }
}
