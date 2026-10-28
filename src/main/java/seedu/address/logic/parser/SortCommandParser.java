package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the rating sort field and optional direction, in any letter case. */
public class SortCommandParser implements Parser<SortCommand> {
    @Override
    public SortCommand parse(String args) throws ParseException {
        String[] parts = args.trim().toLowerCase(Locale.ROOT).split("\\s+");
        if (parts.length > 2 || !"rating".equals(parts[0])
                || parts.length == 2 && !"asc".equals(parts[1]) && !"desc".equals(parts[1])) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
        return new SortCommand(parts.length == 2 && "asc".equals(parts[1]));
    }
}
