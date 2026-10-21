package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;

/**
 * Sets the selection status of a player identified by their index in the displayed list.
 * The {@code keep}, {@code cut} and {@code pending} commands each create this command with their own status.
 */
public class SetStatusCommand extends Command {

    public static final String COMMAND_WORD_KEEP = "keep";
    public static final String COMMAND_WORD_CUT = "cut";
    public static final String COMMAND_WORD_PENDING = "pending";

    public static final String MESSAGE_SUCCESS = "Marked %1$s as %2$s.";
    public static final String MESSAGE_ALREADY_SET = "%1$s is already marked as %2$s.";

    private static final Logger logger = LogsCenter.getLogger(SetStatusCommand.class);

    private final Index targetIndex;
    private final SelectionStatus newStatus;

    /**
     * Creates a command that sets the selection status of the player at {@code targetIndex} to {@code newStatus}.
     */
    public SetStatusCommand(Index targetIndex, SelectionStatus newStatus) {
        requireAllNonNull(targetIndex, newStatus);
        this.targetIndex = targetIndex;
        this.newStatus = newStatus;
    }

    /**
     * Returns the command word that sets a player's status to {@code status}.
     */
    public static String getCommandWord(SelectionStatus status) {
        return switch (status) {
            case KEPT -> COMMAND_WORD_KEEP;
            case CUT -> COMMAND_WORD_CUT;
            case PENDING -> COMMAND_WORD_PENDING;
        };
    }

    /**
     * Returns the usage message of the command word that sets a player's status to {@code status}.
     */
    public static String getUsage(SelectionStatus status) {
        String commandWord = getCommandWord(status);
        return commandWord + ": Marks the player at the given index in the displayed list as "
                + describe(status) + ".\n"
                + "Parameters: INDEX (must be a positive whole number)\n"
                + "Example: " + commandWord + " 3";
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person playerToMark = lastShownList.get(targetIndex.getZeroBased());
        if (playerToMark.getSelectionStatus() == newStatus) {
            return new CommandResult(String.format(MESSAGE_ALREADY_SET, playerToMark.getName(), describe(newStatus)));
        }

        Person markedPlayer = playerToMark.withSelectionStatus(newStatus);
        // The displayed list is not reset, so a coach working through search results stays on them
        model.setPerson(playerToMark, markedPlayer);
        logger.info("Marked " + markedPlayer.getName() + " as " + describe(newStatus));

        return new CommandResult(String.format(MESSAGE_SUCCESS, markedPlayer.getName(), describe(newStatus)));
    }

    /**
     * Returns {@code status} as it appears in messages to the coach, e.g. {@code kept}.
     */
    private static String describe(SelectionStatus status) {
        return status.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SetStatusCommand otherCommand)) {
            return false;
        }

        return targetIndex.equals(otherCommand.targetIndex)
                && newStatus == otherCommand.newStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("newStatus", newStatus)
                .toString();
    }
}
