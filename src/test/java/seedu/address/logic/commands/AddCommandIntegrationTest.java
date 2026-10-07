package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_newPerson_appendedToEndOfList() throws Exception {
        Person validPerson = new PersonBuilder().build();

        new AddCommand(validPerson).execute(model);

        List<Person> shownList = model.getFilteredPersonList();
        assertEquals(validPerson, shownList.get(shownList.size() - 1));
    }

    @Test
    public void execute_filteredList_showsAllPersonsAfterAdd() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_samePhoneDifferentName_success() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person sibling = new PersonBuilder().withName("Sibling Of First").withPhone(personInList.getPhone().value)
                .build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(sibling);

        assertCommandSuccess(new AddCommand(sibling), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(sibling)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicateNameDifferentCaseAndPhone_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person duplicate = new PersonBuilder().withName(personInList.getName().fullName.toUpperCase())
                .withPhone("90001234").build();
        assertCommandFailure(new AddCommand(duplicate), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

}
