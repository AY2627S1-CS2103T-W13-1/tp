package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;
import seedu.address.model.person.TagsContainAnyPredicate;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class TagCommandTest {
    @Test
    public void execute_replacesTags_preservesEveryOtherFieldAndCopiesInput() throws Exception {
        Person original = new PersonBuilder().withRating("10").withJerseyNumber("23")
                .withSelectionStatus(SelectionStatus.KEPT).withInjuryStatus(InjuryStatus.INJURED)
                .withRemark("Strong passing").withTags("Striker").build();
        ModelManager model = new ModelManager();
        model.addPerson(original);
        Set<Tag> requested = new HashSet<>(Set.of(new Tag("Captain")));
        TagCommand command = new TagCommand(Index.fromOneBased(1), requested);
        requested.clear();

        command.execute(model);

        assertEquals(new PersonBuilder(original).withTags("Captain").build(),
                model.getFilteredPersonList().getFirst());
        model.updateFilteredPersonList(new TagsContainAnyPredicate(Set.of(new Tag("Captain"))));
        new TagCommand(Index.fromOneBased(1), Set.of()).execute(model);
        assertEquals(0, model.getFilteredPersonList().size());
        assertEquals(new PersonBuilder(original).withTags().build(), model.getAddressBook().getPersonList().getFirst());
    }

    @Test
    public void execute_invalidIndexAndNullModel_failWithoutAddingPlayers() {
        ModelManager model = new ModelManager();
        TagCommand command = new TagCommand(Index.fromOneBased(1), Set.of());
        assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals(0, model.getAddressBook().getPersonList().size());
        assertThrows(NullPointerException.class, () -> command.execute(null));
        assertThrows(NullPointerException.class, () -> new TagCommand(null, Set.of()));
        assertThrows(NullPointerException.class, () -> new TagCommand(Index.fromOneBased(1), null));
    }

    @Test
    public void equals_comparesIndexAndReplacementTags() {
        TagCommand command = new TagCommand(Index.fromOneBased(1), Set.of(new Tag("Captain")));
        assertEquals(command, command);
        assertEquals(command, new TagCommand(Index.fromOneBased(1), Set.of(new Tag("Captain"))));
        assertFalse(command.equals(new TagCommand(Index.fromOneBased(2), Set.of(new Tag("Captain")))));
        assertFalse(command.equals(new TagCommand(Index.fromOneBased(1), Set.of())));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }
}
