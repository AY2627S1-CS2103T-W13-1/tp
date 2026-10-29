package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    public void execute_appendsTags_preservesEveryOtherFieldAndCopiesInput() throws Exception {
        Person original = new PersonBuilder().withRating("10").withJerseyNumber("23")
                .withSelectionStatus(SelectionStatus.KEPT).withInjuryStatus(InjuryStatus.INJURED)
                .withRemark("Strong passing").withTags("Striker").build();
        ModelManager model = new ModelManager();
        model.addPerson(original);
        Set<Tag> requested = new HashSet<>(Set.of(new Tag("Captain")));
        TagCommand command = new TagCommand(Index.fromOneBased(1), requested);
        requested.clear();

        CommandResult result = command.execute(model);
        assertEquals(String.format(TagCommand.MESSAGE_SUCCESS, original.getName(), "Captain, Striker"),
                result.getFeedbackToUser());

        assertEquals(new PersonBuilder(original).withTags("Captain", "Striker").build(),
                model.getFilteredPersonList().getFirst());
        model.updateFilteredPersonList(new TagsContainAnyPredicate(Set.of(new Tag("Captain"))), "filter t/Captain");
        CommandResult cleared = new TagCommand(Index.fromOneBased(1), Set.of()).execute(model);
        assertEquals(String.format(TagCommand.MESSAGE_CLEARED, original.getName())
                + "\n0 players match the current command: filter t/Captain", cleared.getFeedbackToUser());
        assertEquals(0, model.getFilteredPersonList().size());
        assertEquals(new PersonBuilder(original).withTags().build(), model.getAddressBook().getPersonList().getFirst());
    }

    @Test
    public void execute_existingTagsAndCaseVariants_reportsNoChangeAndPreservesSpelling() throws Exception {
        ModelManager model = new ModelManager();
        Person original = new PersonBuilder().withTags("Captain", "Striker").build();
        model.addPerson(original);
        CommandResult unchanged = new TagCommand(Index.fromOneBased(1), original.getTags()).execute(model);
        assertEquals(String.format(EditCommand.MESSAGE_NO_CHANGE, original.getName()), unchanged.getFeedbackToUser());

        CommandResult repeated = new TagCommand(Index.fromOneBased(1), Set.of(new Tag("captain"))).execute(model);
        assertEquals(String.format(EditCommand.MESSAGE_NO_CHANGE, original.getName()), repeated.getFeedbackToUser());
        assertSame(original, model.getFilteredPersonList().getFirst());
        assertEquals(Set.of("Captain", "Striker"), model.getFilteredPersonList().getFirst().getTags().stream()
                .map(Tag::toString).collect(Collectors.toSet()));
    }

    @Test
    public void execute_overlappingTags_addsNewLabelsAndPreservesExistingSpelling() throws Exception {
        ModelManager model = new ModelManager();
        Person original = new PersonBuilder().withTags("Captain", "Striker").build();
        model.addPerson(original);
        model.updateFilteredPersonList(new TagsContainAnyPredicate(Set.of(new Tag("Striker"))));

        CommandResult result = new TagCommand(Index.fromOneBased(1),
                Set.of(new Tag("captain"), new Tag("Trial Group 2"))).execute(model);

        assertEquals(String.format(TagCommand.MESSAGE_SUCCESS, original.getName(), "Captain, Striker, Trial Group 2"),
                result.getFeedbackToUser());
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(Set.of("Captain", "Striker", "Trial Group 2"),
                model.getFilteredPersonList().getFirst().getTags().stream()
                        .map(Tag::toString).collect(Collectors.toSet()));
    }

    @Test
    public void execute_untaggedPlayer_addsRequestedTags() throws Exception {
        ModelManager model = new ModelManager();
        Person original = new PersonBuilder().withTags().build();
        model.addPerson(original);

        new TagCommand(Index.fromOneBased(1), Set.of(new Tag("Captain"))).execute(model);

        assertEquals(new PersonBuilder(original).withTags("Captain").build(),
                model.getFilteredPersonList().getFirst());
    }

    @Test
    public void toString_showsIndexAndTagsToAdd() {
        TagCommand command = new TagCommand(Index.fromOneBased(1), Set.of(new Tag("Captain")));
        assertEquals(TagCommand.class.getCanonicalName() + "{index=" + Index.fromOneBased(1)
                + ", tags=[Captain]}", command.toString());
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
    public void equals_comparesIndexAndTagsToAdd() {
        TagCommand command = new TagCommand(Index.fromOneBased(1), Set.of(new Tag("Captain")));
        assertEquals(command, command);
        assertEquals(command, new TagCommand(Index.fromOneBased(1), Set.of(new Tag("Captain"))));
        assertFalse(command.equals(new TagCommand(Index.fromOneBased(2), Set.of(new Tag("Captain")))));
        assertFalse(command.equals(new TagCommand(Index.fromOneBased(1), Set.of())));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }
}
