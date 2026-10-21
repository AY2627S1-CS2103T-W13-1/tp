package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.SortCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Rating;
import seedu.address.testutil.PersonBuilder;

public class SortCommandTest {
    private final Person low = new PersonBuilder().withName("Low Tan").withRating("0").build();
    private final Person high = new PersonBuilder().withName("High Tan").withRating("10").build();
    private final Person tied = new PersonBuilder().withName("Tied Tan").withRating("10").build();
    private final Person unrated = new PersonBuilder().withName("Unrated Lee").build();

    @Test
    public void equals_comparesSortDirection() {
        SortCommand ascending = new SortCommand(true);
        SortCommand descending = new SortCommand(false);
        assertEquals(ascending, ascending);
        assertEquals(ascending, new SortCommand(true));
        assertEquals(descending, new SortCommand(false));
        assertFalse(ascending.equals(descending));
        assertFalse(ascending.equals(null));
        assertFalse(ascending.equals(new ListCommand()));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SortCommand(true).execute(null));
    }

    @Test
    public void execute_multipleUnratedPlayers_retainsInsertionOrderInBothDirections() {
        ModelManager model = new ModelManager();
        Person anotherUnrated = new PersonBuilder().withName("Another Player").build();
        model.addPerson(unrated);
        model.addPerson(anotherUnrated);
        for (boolean ascending : new boolean[] {true, false}) {
            CommandResult result = new SortCommand(ascending).execute(model);
            assertEquals(List.of(unrated, anotherUnrated), model.getFilteredPersonList());
            assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, ascending ? "ascending" : "descending"),
                    result.getFeedbackToUser());
        }
    }

    @Test
    public void execute_bothDirections_keepsUnratedLastAndTiesInInsertionOrder() {
        ModelManager model = createModel();
        new SortCommand(false).execute(model);
        assertEquals(List.of(high, tied, low, unrated), model.getFilteredPersonList());
        new SortCommand(true).execute(model);
        assertEquals(List.of(low, high, tied, unrated), model.getFilteredPersonList());
        assertEquals(List.of(unrated, low, high, tied), model.getAddressBook().getPersonList());
        new ListCommand().execute(model);
        assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_filteredList_keepsFilterAndUsesDisplayedIndices() throws Exception {
        ModelManager model = createModel();
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Tan")));
        new SortCommand(false).execute(model);
        assertEquals(List.of(high, tied, low), model.getFilteredPersonList());
        new RateCommand(Index.fromOneBased(1), new Rating("0")).execute(model);
        assertEquals(List.of(tied, low, new PersonBuilder(high).withRating("0").build()),
                model.getFilteredPersonList());
        new DeleteCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(false, model.hasPerson(tied));
    }

    @Test
    public void execute_emptyAndUnratedLists_success() {
        ModelManager model = new ModelManager();
        new SortCommand(false).execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        model.addPerson(unrated);
        new SortCommand(true).execute(model);
        assertEquals(List.of(unrated), model.getFilteredPersonList());
    }

    @Test
    public void execute_addEditAndInvalidCommands_preservesSorting() throws Exception {
        ModelManager model = createModel();
        new SortCommand(false).execute(model);
        Person added = new PersonBuilder().withName("Added Player").withRating("5").build();
        model.addPerson(added);
        assertEquals(List.of(high, tied, added, low, unrated), model.getFilteredPersonList());
        EditCommand.EditPersonDescriptor edit = new EditCommand.EditPersonDescriptor();
        edit.setPhone(new Phone("12345678"));
        new EditCommand(Index.fromOneBased(3), edit).execute(model);
        assertEquals(new Rating("5"), model.getFilteredPersonList().get(2).getRating());
        List<Person> before = List.copyOf(model.getFilteredPersonList());
        assertThrows(ParseException.class, () -> new SortCommandParser().parse("rating invalid"));
        assertEquals(before, model.getFilteredPersonList());
    }

    private ModelManager createModel() {
        ModelManager model = new ModelManager();
        List.of(unrated, low, high, tied).forEach(model::addPerson);
        return model;
    }
}
