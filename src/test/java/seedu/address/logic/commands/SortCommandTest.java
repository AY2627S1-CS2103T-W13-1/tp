package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.ModelManager;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Rating;
import seedu.address.testutil.PersonBuilder;

public class SortCommandTest {
    private final Person low = new PersonBuilder().withName("Low Tan").withRating("0").build();
    private final Person high = new PersonBuilder().withName("High Tan").withRating("10").build();
    private final Person tied = new PersonBuilder().withName("Tied Tan").withRating("10").build();
    private final Person unrated = new PersonBuilder().withName("Unrated Lee").build();

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

    private ModelManager createModel() {
        ModelManager model = new ModelManager();
        List.of(unrated, low, high, tied).forEach(model::addPerson);
        return model;
    }
}
