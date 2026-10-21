package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Rating;
import seedu.address.testutil.PersonBuilder;

public class RateCommandTest {
    @Test
    public void equals_comparesDisplayedIndexAndRating() {
        RateCommand command = new RateCommand(Index.fromOneBased(1), new Rating("8"));
        assertEquals(command, command);
        assertEquals(command, new RateCommand(Index.fromOneBased(1), new Rating("8")));
        assertFalse(command.equals(new RateCommand(Index.fromOneBased(2), new Rating("8"))));
        assertFalse(command.equals(new RateCommand(Index.fromOneBased(1), new Rating("9"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RateCommand(null, new Rating("8")));
        assertThrows(NullPointerException.class, () -> new RateCommand(Index.fromOneBased(1), null));
    }

    @Test
    public void execute_filteredList_ratesDisplayedPlayerAndPreservesFilter() throws Exception {
        ModelManager model = new ModelManager();
        Person amy = new PersonBuilder().withName("Amy Bee").build();
        Person bob = new PersonBuilder().withName("Bob Tan").build();
        model.addPerson(amy);
        model.addPerson(bob);
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Bob")));
        CommandResult result = new RateCommand(Index.fromOneBased(1), new Rating("8")).execute(model);
        Person ratedBob = new PersonBuilder(bob).withRating("8").build();
        assertEquals(List.of(ratedBob), model.getFilteredPersonList());
        assertEquals(List.of(amy, ratedBob), model.getAddressBook().getPersonList());
        assertEquals("Rated player: Bob Tan; Rating: 8/10", result.getFeedbackToUser());
    }

    @Test
    public void execute_playerWithJerseyNumber_keepsJerseyNumber() throws Exception {
        ModelManager model = new ModelManager();
        Person amy = new PersonBuilder().withJerseyNumber("10").build();
        model.addPerson(amy);
        new RateCommand(Index.fromOneBased(1), new Rating("8")).execute(model);
        assertEquals(List.of(new PersonBuilder(amy).withRating("8").build()), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_outOfBounds_doesNotChangePlayer() {
        ModelManager model = new ModelManager();
        Person amy = new PersonBuilder().build();
        model.addPerson(amy);
        assertThrows(CommandException.class, () ->
                new RateCommand(Index.fromOneBased(2), new Rating("8")).execute(model));
        assertEquals(List.of(amy), model.getFilteredPersonList());
        assertThrows(IllegalArgumentException.class, () ->
                new RateCommand(Index.fromOneBased(1), Rating.UNRATED));
    }

    @Test
    public void execute_ratingThenEdit_preservesRatingAndIdentity() throws Exception {
        ModelManager model = new ModelManager();
        Person amy = new PersonBuilder().build();
        model.addPerson(amy);
        new RateCommand(Index.fromOneBased(1), new Rating("0")).execute(model);
        Person rated = model.getFilteredPersonList().get(0);
        assertFalse(amy.equals(rated));
        assertEquals(true, amy.isSamePerson(rated));
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setName(new Name("Amy Tan"));
        new EditCommand(Index.fromOneBased(1), descriptor).execute(model);
        assertEquals(new Rating("0"), model.getFilteredPersonList().get(0).getRating());
        new RateCommand(Index.fromOneBased(1), new Rating("10")).execute(model);
        assertEquals(new Rating("10"), model.getFilteredPersonList().get(0).getRating());
    }
}
