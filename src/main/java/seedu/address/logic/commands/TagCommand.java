package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/** Appends to or clears a displayed player's tags using the shared player-edit operation. */
public class TagCommand extends Command {
    /** Command word for updating tags. */
    public static final String COMMAND_WORD = "tag";
    /** Syntax guidance including the explicit tag-clearing form. */
    public static final String MESSAGE_USAGE = "tag: Adds tags to a player's existing tags.\n"
            + "Parameters: INDEX (positive integer) t/TAG [t/TAG]...\n"
            + "Use a single empty t/ to clear tags.\nExample: tag 1 t/Striker t/Captain";
    /** Guidance for the retired edit-tag syntax. */
    public static final String MESSAGE_USE_TAG = "Use 'tag INDEX t/TAG [t/TAG]...' to add tags, "
            + "or 'tag INDEX t/' to clear them. The edit command changes only name, phone, and jersey number.";

    /** Feedback naming the player and their complete updated tags. */
    public static final String MESSAGE_SUCCESS = "Updated tags for %1$s: %2$s";
    /** Feedback explicitly confirming removal of all tags. */
    public static final String MESSAGE_CLEARED = "%1$s: tags cleared.";

    private final Index index;
    private final Set<Tag> tags;

    /** Creates a tag addition with a defensive copy; an empty set requests clearing all tags. */
    public TagCommand(Index index, Set<Tag> tags) {
        this.index = requireNonNull(index);
        this.tags = Set.copyOf(requireNonNull(tags));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayed = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayed.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person player = displayed.get(index.getZeroBased());
        Set<Tag> updatedTags = getUpdatedTags(player);
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setTags(updatedTags);
        CommandResult result = new EditCommand(index, descriptor).execute(model);
        if (updatedTags.equals(player.getTags())) {
            return result;
        }
        List<String> tagNames = getTagNames(updatedTags);
        String feedback = tagNames.isEmpty()
                ? String.format(MESSAGE_CLEARED, player.getName())
                : String.format(MESSAGE_SUCCESS, player.getName(), String.join(", ", tagNames));
        return new CommandResult(Messages.appendCurrentListSummary(feedback, model));
    }

    private Set<Tag> getUpdatedTags(Person player) {
        if (tags.isEmpty()) {
            return Set.of();
        }
        Set<Tag> updatedTags = new HashSet<>(player.getTags());
        updatedTags.addAll(tags);
        return updatedTags;
    }

    private static List<String> getTagNames(Set<Tag> tags) {
        return tags.stream().map(Tag::toString).sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).add("tags", tags).toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof TagCommand otherCommand
                && index.equals(otherCommand.index) && tags.equals(otherCommand.tags);
    }
}
