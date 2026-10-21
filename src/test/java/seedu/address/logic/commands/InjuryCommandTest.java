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

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

public class InjuryCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_updatesOnlyTargetPlayer() {
        Person player = model.getFilteredPersonList().getFirst();
        Person updatedPlayer = new PersonBuilder(player).withInjuryStatus(InjuryStatus.INJURED).build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(player, updatedPlayer);

        assertCommandSuccess(new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED), model,
                String.format(InjuryCommand.MESSAGE_SUCCESS, player.getName(), updatedPlayer.getInjuryStatus()),
                expectedModel);
    }

    @Test
    public void execute_replaceAndRepeatStatus_success() throws Exception {
        new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED).execute(model);
        assertEquals(InjuryStatus.INJURED, model.getFilteredPersonList().getFirst().getInjuryStatus());
        new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED).execute(model);
        assertEquals(InjuryStatus.INJURED, model.getFilteredPersonList().getFirst().getInjuryStatus());
        new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.FIT).execute(model);
        assertEquals(InjuryStatus.FIT, model.getFilteredPersonList().getFirst().getInjuryStatus());
        new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.FIT).execute(model);
        assertEquals(InjuryStatus.FIT, model.getFilteredPersonList().getFirst().getInjuryStatus());
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndPreservesFilter() throws Exception {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person target = model.getFilteredPersonList().getFirst();
        Person updatedPlayer = new PersonBuilder(target).withInjuryStatus(InjuryStatus.INJURED).build();
        new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED).execute(model);

        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(updatedPlayer, model.getFilteredPersonList().getFirst());
        assertEquals(updatedPlayer, model.getAddressBook().getPersonList().get(1));
    }

    @Test
    public void execute_tagFilteredPlayer_preservesOtherFieldsAndFilter() throws Exception {
        Person original = new PersonBuilder().withTags("Striker").withRating("8")
                .withSelectionStatus(SelectionStatus.KEPT).withRemark("Strong left foot").build();
        Person other = new PersonBuilder().withName("Other Player").withTags("Goalkeeper").build();
        AddressBook team = new AddressBook();
        team.setPersons(List.of(original, other));
        Model filteredModel = new ModelManager(team, new UserPrefs());
        new AddressBookParser().parseCommand("filter t/Striker").execute(filteredModel);

        new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED).execute(filteredModel);

        Person expected = new PersonBuilder(original).withInjuryStatus(InjuryStatus.INJURED).build();
        assertEquals(List.of(expected), filteredModel.getFilteredPersonList());
        assertEquals(List.of(expected, other), filteredModel.getAddressBook().getPersonList());
    }

    @Test
    public void execute_invalidIndexes_leavesModelUnchanged() {
        assertCommandFailure(new InjuryCommand(Index.fromOneBased(100), InjuryStatus.INJURED), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandFailure(new InjuryCommand(INDEX_SECOND_PERSON, InjuryStatus.INJURED), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        assertCommandFailure(new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED), emptyModel,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_editNameAndPhone_preservesInjuryStatus() throws Exception {
        new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED).execute(model);
        EditCommand.EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("New Player Name").withPhone("98765432").build();
        new EditCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        Person player = model.getFilteredPersonList().getFirst();
        assertEquals("New Player Name", player.getName().fullName);
        assertEquals("98765432", player.getPhone().value);
        assertEquals(InjuryStatus.INJURED, player.getInjuryStatus());
    }

    @Test
    public void constructorAndExecute_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new InjuryCommand(null, InjuryStatus.INJURED));
        assertThrows(NullPointerException.class, () -> new InjuryCommand(INDEX_FIRST_PERSON, null));
        InjuryCommand command = new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals_comparesIndexAndValue() {
        InjuryCommand command = new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED);
        assertEquals(command, command);
        assertEquals(command, new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED));
        assertNotEquals(command, new InjuryCommand(INDEX_SECOND_PERSON, InjuryStatus.INJURED));
        assertNotEquals(command, new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.FIT));
        assertNotEquals(command, null);
        assertNotEquals(command, "injury");
        assertTrue(command.toString().contains("injuryStatus="));
    }
}
