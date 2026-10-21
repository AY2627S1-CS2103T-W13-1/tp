package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code SetStatusCommand}, which backs the {@code keep}, {@code cut} and {@code pending} commands.
 */
public class SetStatusCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SetStatusCommand(null, SelectionStatus.KEPT));
        assertThrows(NullPointerException.class, () -> new SetStatusCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_keepValidIndexUnfilteredList_success() {
        assertMarkSuccess(INDEX_FIRST_PERSON, SelectionStatus.KEPT, "kept");
    }

    @Test
    public void execute_cutValidIndexUnfilteredList_success() {
        assertMarkSuccess(INDEX_SECOND_PERSON, SelectionStatus.CUT, "cut");
    }

    @Test
    public void execute_pendingAfterKeep_success() {
        Person playerToMark = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(playerToMark, playerToMark.withSelectionStatus(SelectionStatus.KEPT));

        assertMarkSuccess(INDEX_FIRST_PERSON, SelectionStatus.PENDING, "pending");
    }

    @Test
    public void execute_sameStatus_reportsAlreadyMarked() {
        Person playerToMark = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        SetStatusCommand command = new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.PENDING);

        String expectedMessage = String.format(SetStatusCommand.MESSAGE_ALREADY_SET,
                playerToMark.getName(), "pending");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_keepsFilter() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person playerToMark = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        SetStatusCommand command = new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.CUT);

        String expectedMessage = String.format(SetStatusCommand.MESSAGE_SUCCESS, playerToMark.getName(), "cut");
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(playerToMark, playerToMark.withSelectionStatus(SelectionStatus.CUT));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        SetStatusCommand command = new SetStatusCommand(outOfBoundIndex, SelectionStatus.KEPT);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        SetStatusCommand command = new SetStatusCommand(outOfBoundIndex, SelectionStatus.CUT);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void getCommandWord_eachStatus_returnsMatchingWord() {
        assertEquals("keep", SetStatusCommand.getCommandWord(SelectionStatus.KEPT));
        assertEquals("cut", SetStatusCommand.getCommandWord(SelectionStatus.CUT));
        assertEquals("pending", SetStatusCommand.getCommandWord(SelectionStatus.PENDING));
    }

    @Test
    public void getUsage_keep_mentionsWordAndExample() {
        String usage = SetStatusCommand.getUsage(SelectionStatus.KEPT);
        assertTrue(usage.startsWith("keep: Marks the player at the given index in the displayed list as kept."));
        assertTrue(usage.endsWith("Example: keep 3"));
    }

    @Test
    public void equals() {
        SetStatusCommand keepFirstCommand = new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.KEPT);
        SetStatusCommand keepSecondCommand = new SetStatusCommand(INDEX_SECOND_PERSON, SelectionStatus.KEPT);
        SetStatusCommand cutFirstCommand = new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.CUT);

        // same object -> returns true
        assertTrue(keepFirstCommand.equals(keepFirstCommand));

        // same values -> returns true
        assertTrue(keepFirstCommand.equals(new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.KEPT)));

        // different types -> returns false
        assertFalse(keepFirstCommand.equals(1));

        // null -> returns false
        assertFalse(keepFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(keepFirstCommand.equals(keepSecondCommand));

        // different status -> returns false
        assertFalse(keepFirstCommand.equals(cutFirstCommand));
    }

    @Test
    public void toStringMethod() {
        SetStatusCommand command = new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.KEPT);
        String expected = SetStatusCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", newStatus=" + SelectionStatus.KEPT + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Asserts that marking the player at {@code index} of the unfiltered list as {@code newStatus}
     * succeeds, shows the status as {@code statusInMessage}, and changes only that player's status.
     */
    private void assertMarkSuccess(Index index, SelectionStatus newStatus, String statusInMessage) {
        Person playerToMark = model.getFilteredPersonList().get(index.getZeroBased());
        SetStatusCommand command = new SetStatusCommand(index, newStatus);

        String expectedMessage = String.format(SetStatusCommand.MESSAGE_SUCCESS,
                playerToMark.getName(), statusInMessage);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(playerToMark, playerToMark.withSelectionStatus(newStatus));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }
}
