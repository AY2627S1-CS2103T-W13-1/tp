package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.TagsContainAnyPredicate;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class FilterCommandTest {
    private static final Person STRIKER = new PersonBuilder().withName("Amy").withTags("Striker", "Captain").build();
    private static final Person GOALKEEPER = new PersonBuilder().withName("Bob").withTags("Goalkeeper").build();
    private static final Person CAPTAIN = new PersonBuilder().withName("Charlie").withTags("Captain").build();

    private final Model model = createModel();

    @Test
    public void execute_orMatching_preservesOrderAndReturnsEachPlayerOnce() {
        AddressBook original = new AddressBook(model.getAddressBook());
        FilterCommand command = commandFor("striker", "captain");
        assertEquals("2 players listed!", command.execute(model).getFeedbackToUser());
        assertEquals(List.of(STRIKER, CAPTAIN), model.getFilteredPersonList());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_oneZeroAndEmptyTeam_returnsCorrectFeedback() {
        assertEquals("1 player listed!", commandFor("striker").execute(model).getFeedbackToUser());
        assertEquals(List.of(STRIKER), model.getFilteredPersonList());
        assertEquals("0 players listed!", commandFor("strik").execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals("0 players listed!", commandFor("Striker").execute(new ModelManager()).getFeedbackToUser());
    }

    @Test
    public void execute_filtersWholeTeamAndInteroperatesWithFindAndList() {
        new FindCommand(new NameContainsKeywordsPredicate(List.of("Amy"))).execute(model);
        commandFor("Goalkeeper").execute(model);
        assertEquals(List.of(GOALKEEPER), model.getFilteredPersonList());
        commandFor("Captain").execute(model);
        assertEquals(List.of(STRIKER, CAPTAIN), model.getFilteredPersonList());
        new FindCommand(new NameContainsKeywordsPredicate(List.of("Bob"))).execute(model);
        assertEquals(List.of(GOALKEEPER), model.getFilteredPersonList());
        commandFor("Captain").execute(model);
        new ListCommand().execute(model);
        assertEquals(List.of(STRIKER, GOALKEEPER, CAPTAIN), model.getFilteredPersonList());
    }

    @Test
    public void execute_deleteUsesFilteredIndexAndRetainsTagFilter() throws Exception {
        commandFor("Captain").execute(model);
        new DeleteCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(List.of(CAPTAIN), model.getFilteredPersonList());
        assertTrue(model.hasPerson(GOALKEEPER));
        assertFalse(model.hasPerson(STRIKER));
        new DeleteCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void constructorAndExecute_nullsRejected() {
        assertThrows(NullPointerException.class, () -> new FilterCommand(null));
        assertThrows(NullPointerException.class, () -> commandFor("Striker").execute(null));
    }

    @Test
    public void execute_inconsistentModel_throwsAssertionError() {
        Model inconsistentModel = new ModelManager() {
            @Override
            public ReadOnlyAddressBook getAddressBook() {
                return new AddressBook();
            }
        };
        // Simulate a broken model that exposes a filtered player but reports an empty roster.
        inconsistentModel.addPerson(STRIKER);
        FilterCommand command = commandFor("Striker");
        assertThrows(AssertionError.class, "A filter cannot add players", () -> command.execute(inconsistentModel));
    }

    @Test
    public void equalsAndToString() {
        FilterCommand command = commandFor("Striker");
        assertEquals(command, command);
        assertEquals(command, commandFor("striker"));
        assertFalse(command.equals(commandFor("Goalkeeper")));
        assertFalse(command.equals(null));
        assertFalse(command.equals("filter"));
        assertTrue(command.toString().contains("predicate="));
    }

    private static Model createModel() {
        Model model = new ModelManager();
        List.of(STRIKER, GOALKEEPER, CAPTAIN).forEach(model::addPerson);
        return model;
    }

    private static FilterCommand commandFor(String... labels) {
        Set<Tag> tags = Arrays.stream(labels).map(Tag::new).collect(Collectors.toSet());
        return new FilterCommand(new TagsContainAnyPredicate(tags));
    }
}
