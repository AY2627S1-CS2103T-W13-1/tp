package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
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

    private final EditCommand editCommand;

    /** Creates a tag update, defensively copying tags through the edit descriptor. */
    public TagCommand(Index index, Set<Tag> tags) {
        requireNonNull(tags);
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setTags(tags);
        editCommand = new EditCommand(index, descriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        // Reuse field preservation, no-change feedback, and the current filter's update semantics.
        return editCommand.execute(model);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof TagCommand otherCommand
                && editCommand.equals(otherCommand.editCommand);
    }
}
