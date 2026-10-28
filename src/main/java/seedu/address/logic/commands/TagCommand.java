package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

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

/** Replaces or clears a displayed player's tags using the shared player-edit operation. */
public class TagCommand extends Command {
    /** Command word for updating tags. */
    public static final String COMMAND_WORD = "tag";
    /** Syntax guidance including the explicit tag-clearing form. */
    public static final String MESSAGE_USAGE = "tag: Replaces a player's tags.\n"
            + "Parameters: INDEX (positive integer) t/TAG [t/TAG]...\n"
            + "Use a single empty t/ to clear tags.\nExample: tag 1 t/Striker t/Captain";
    /** Guidance for the retired edit-tag syntax. */
    public static final String MESSAGE_USE_TAG = "Use 'tag INDEX t/TAG [t/TAG]...' to replace tags, "
            + "or 'tag INDEX t/' to clear them. The edit command changes only name, phone, and jersey number.";

    /** Feedback naming the player and their replacement tags. */
    public static final String MESSAGE_SUCCESS = "Updated tags for %1$s: %2$s";
    /** Feedback explicitly confirming removal of all tags. */
    public static final String MESSAGE_CLEARED = "%1$s: tags cleared.";

    private final Index index;
    private final List<String> tagNames;
    private final EditCommand editCommand;

    /** Creates a tag update, defensively copying tags through the edit descriptor. */
    public TagCommand(Index index, Set<Tag> tags) {
        this.index = requireNonNull(index);
        requireNonNull(tags);
        tagNames = getTagNames(tags);
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setTags(tags);
        editCommand = new EditCommand(index, descriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayed = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayed.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person player = displayed.get(index.getZeroBased());
        boolean hasSameTags = tagNames.equals(getTagNames(player.getTags()));
        CommandResult result = editCommand.execute(model);
        if (hasSameTags) {
            return result;
        }
        String feedback = tagNames.isEmpty()
                ? String.format(MESSAGE_CLEARED, player.getName())
                : String.format(MESSAGE_SUCCESS, player.getName(), String.join(", ", tagNames));
        return new CommandResult(feedback);
    }

    private static List<String> getTagNames(Set<Tag> tags) {
        return tags.stream().map(Tag::toString).sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("editCommand", editCommand).toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof TagCommand otherCommand
                && editCommand.equals(otherCommand.editCommand);
    }
}
