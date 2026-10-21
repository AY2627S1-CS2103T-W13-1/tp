package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.Person;

/**
 * Updates the injury status of a player in the displayed list.
 */
public class InjuryCommand extends Command {
    public static final String COMMAND_WORD = "injury";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Updates a player's injury status.\n"
            + "Parameters: INDEX (must be a positive integer) " + PREFIX_INJURY + "STATUS (injured or fit)\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_INJURY + "injured";
    public static final String MESSAGE_SUCCESS = "Updated injury status for %1$s: %2$s";

    private static final Logger logger = LogsCenter.getLogger(InjuryCommand.class);

    private final Index index;
    private final InjuryStatus injuryStatus;

    /**
     * Constructs a command for the given displayed index and injury status.
     */
    public InjuryCommand(Index index, InjuryStatus injuryStatus) {
        requireAllNonNull(index, injuryStatus);
        this.index = index;
        this.injuryStatus = injuryStatus;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        List<Person> displayedPlayers = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayedPlayers.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person player = displayedPlayers.get(index.getZeroBased());
        Person updatedPlayer = player.withInjuryStatus(injuryStatus);
        assert player.isSamePerson(updatedPlayer) : "Updating injury status must preserve player identity";
        model.setPerson(player, updatedPlayer);
        logger.fine("Updated player injury status at displayed index " + index.getOneBased());
        return new CommandResult(String.format(MESSAGE_SUCCESS, updatedPlayer.getName(),
                injuryStatus));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof InjuryCommand otherCommand
                && index.equals(otherCommand.index) && injuryStatus.equals(otherCommand.injuryStatus);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).add("injuryStatus", injuryStatus).toString();
    }
}
