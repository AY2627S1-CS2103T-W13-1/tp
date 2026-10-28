package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Requests saving the current team without changing players or the displayed list.
 * LogicManager writes the data after executing this command, as for other saving commands.
 */
public class SaveCommand extends Command {
    public static final String COMMAND_WORD = "save";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Saves the current team to disk.\n"
            + "Parameters: none. Use 'save' on its own.";
    public static final String MESSAGE_SUCCESS = "Saved the current team to disk.";
    public static final String MESSAGE_FAILURE = "The current team could not be saved to disk. "
            + "Fix the problem and type 'save' to retry before closing CouchCoach, or unsaved changes will be lost.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
