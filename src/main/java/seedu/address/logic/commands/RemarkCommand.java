package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Updates the remark of a player in the displayed list.
 */
public class RemarkCommand extends Command {
    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Updates a player's remark.\n"
            + "Parameters: INDEX (must be a positive integer) " + PREFIX_REMARK + "REMARK (leave empty to clear)\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_REMARK + "Strong left foot";
    public static final String MESSAGE_SUCCESS = "Updated remark for %1$s: %2$s";

    private static final Logger logger = LogsCenter.getLogger(RemarkCommand.class);

    private final Index index;
    private final Remark remark;

    /**
     * Constructs a command for the given displayed index and remark.
     */
    public RemarkCommand(Index index, Remark remark) {
        requireAllNonNull(index, remark);
        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        List<Person> displayedPlayers = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayedPlayers.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person player = displayedPlayers.get(index.getZeroBased());
        Person updatedPlayer = player.withRemark(remark);
        assert player.isSamePerson(updatedPlayer) : "Updating remark must preserve player identity";
        model.setPerson(player, updatedPlayer);
        logger.fine("Updated player remark at displayed index " + index.getOneBased());
        return new CommandResult(String.format(MESSAGE_SUCCESS, updatedPlayer.getName(),
                remark.value.isEmpty() ? "(cleared)" : remark));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof RemarkCommand otherCommand
                && index.equals(otherCommand.index) && remark.equals(otherCommand.remark);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).add("remark", remark).toString();
    }
}
