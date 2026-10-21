package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import seedu.address.model.person.Remark;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_updatesOnlyTargetPlayer() {
        Person player = model.getFilteredPersonList().getFirst();
        Person updatedPlayer = new PersonBuilder(player).withRemark("Strong left foot").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(player, updatedPlayer);

        assertCommandSuccess(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Strong left foot")), model,
                String.format(RemarkCommand.MESSAGE_SUCCESS, player.getName(), updatedPlayer.getRemark()),
                expectedModel);
    }

    @Test
    public void execute_replaceAndClearRemark_success() throws Exception {
        new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Strong left foot")).execute(model);
        new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Improved passing")).execute(model);
        assertEquals(new Remark("Improved passing"), model.getFilteredPersonList().getFirst().getRemark());
        new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")).execute(model);
        assertEquals(new Remark(""), model.getFilteredPersonList().getFirst().getRemark());
        new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")).execute(model);
        assertEquals(new Remark(""), model.getFilteredPersonList().getFirst().getRemark());
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndPreservesFilter() throws Exception {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person target = model.getFilteredPersonList().getFirst();
        Person updatedPlayer = new PersonBuilder(target).withRemark("Strong left foot").build();
        new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Strong left foot")).execute(model);

        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(updatedPlayer, model.getFilteredPersonList().getFirst());
        assertEquals(updatedPlayer, model.getAddressBook().getPersonList().get(1));
    }

    @Test
    public void execute_invalidIndexes_leavesModelUnchanged() {
        assertCommandFailure(new RemarkCommand(Index.fromOneBased(100), new Remark("Strong left foot")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandFailure(new RemarkCommand(INDEX_SECOND_PERSON, new Remark("Strong left foot")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        assertCommandFailure(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Strong left foot")), emptyModel,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_editNameAndPhone_preservesRemark() throws Exception {
        new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Strong left foot")).execute(model);
        EditCommand.EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("New Player Name").withPhone("98765432").build();
        new EditCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        Person player = model.getFilteredPersonList().getFirst();
        assertEquals("New Player Name", player.getName().fullName);
        assertEquals("98765432", player.getPhone().value);
        assertEquals(new Remark("Strong left foot"), player.getRemark());
    }

    @Test
    public void constructorAndExecute_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemarkCommand(null, new Remark("Strong left foot")));
        assertThrows(NullPointerException.class, () -> new RemarkCommand(INDEX_FIRST_PERSON, null));
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Strong left foot"));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals_comparesIndexAndValue() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Strong left foot"));
        assertEquals(command, command);
        assertEquals(command, new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Strong left foot")));
        assertNotEquals(command, new RemarkCommand(INDEX_SECOND_PERSON, new Remark("Strong left foot")));
        assertNotEquals(command, new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Improved passing")));
        assertNotEquals(command, null);
        assertNotEquals(command, "remark");
        assertTrue(command.toString().contains("remark="));
    }
}
