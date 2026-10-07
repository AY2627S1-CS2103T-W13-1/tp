package seedu.address.logic;

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
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
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

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
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
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
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

        assertFindSuccess("find wei", "2 players listed!", List.of(tan, lim));
        assertFindSuccess("find TAN", "1 player listed!", List.of(tan));
        assertFindSuccess("find wei nur'ain", "3 players listed!", List.of(tan, lim, nur));
        assertFindSuccess("find ming tan ming", "1 player listed!", List.of(tan));
        assertFindSuccess("find We", "0 players listed!", List.of());
        assertFindSuccess("find " + tan.getPhone(), "0 players listed!", List.of());
        assertFindSuccess("find S/O", "1 player listed!", List.of(muthu));
        assertFindSuccess("  find  wei \t ming  ", "2 players listed!", List.of(tan, lim));
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
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
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
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
