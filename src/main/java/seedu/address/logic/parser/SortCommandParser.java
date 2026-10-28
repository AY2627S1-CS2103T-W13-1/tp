package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.Scope;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses a rating sort field, optional scope, and optional direction in any letter case. */
public class SortCommandParser implements Parser<SortCommand> {
    @Override
    public SortCommand parse(String args) throws ParseException {
        String[] parts = args.trim().toLowerCase(Locale.ROOT).split("\\s+");
        if (parts.length > 3 || !"rating".equals(parts[0])) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
        Scope scope = Scope.CURRENT;
        int directionIndex = 1;
        if (parts.length > 1 && "all".equals(parts[1])) {
            scope = Scope.ALL;
            directionIndex++;
        } else if (parts.length > 1 && "squad".equals(parts[1])) {
            scope = Scope.SQUAD;
            directionIndex++;
        }
        if (parts.length > directionIndex + 1 || parts.length > directionIndex
                && !"asc".equals(parts[directionIndex]) && !"desc".equals(parts[directionIndex])) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
        return new SortCommand(parts.length > directionIndex && "asc".equals(parts[directionIndex]), scope);
    }
}
