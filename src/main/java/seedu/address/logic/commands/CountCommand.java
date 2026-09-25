package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Counts the number of persons in the currently displayed list.
 */
public class CountCommand extends Command {

    public static final String COMMAND_WORD = "count";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows the number of persons in the currently displayed list.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "%1$d person(s) in the displayed list.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        int count = model.getFilteredPersonList().size();
        return new CommandResult(String.format(MESSAGE_SUCCESS, count));
    }
}
