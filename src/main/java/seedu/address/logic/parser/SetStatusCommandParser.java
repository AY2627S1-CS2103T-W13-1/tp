package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SetStatusCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.SelectionStatus;

/**
 * Parses the arguments of the {@code keep}, {@code cut} and {@code pending} commands
 * into a {@code SetStatusCommand} that sets the given status.
 */
public class SetStatusCommandParser implements Parser<SetStatusCommand> {

    private final SelectionStatus newStatus;

    /**
     * Creates a parser for the command word that sets a player's status to {@code newStatus}.
     */
    public SetStatusCommandParser(SelectionStatus newStatus) {
        requireNonNull(newStatus);
        this.newStatus = newStatus;
    }

    /**
     * Parses the given {@code String} of arguments into a {@code SetStatusCommand}.
     *
     * @throws ParseException if the user input is not a single valid index.
     */
    public SetStatusCommand parse(String args) throws ParseException {
        Index index = ParserUtil.parseIndex(args, SetStatusCommand.getUsage(newStatus));
        return new SetStatusCommand(index, newStatus);
    }
}
