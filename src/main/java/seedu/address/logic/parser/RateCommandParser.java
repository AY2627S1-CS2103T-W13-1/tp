package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RateCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Rating;

/** Parses a displayed player index and a rating. */
public class RateCommandParser implements Parser<RateCommand> {
    @Override
    public RateCommand parse(String args) throws ParseException {
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RateCommand.MESSAGE_USAGE));
        }
        Index index = ParserUtil.parseIndex(parts[0]);
        if (!Rating.isValidRating(parts[1])) {
            throw new ParseException(Rating.MESSAGE_CONSTRAINTS);
        }
        return new RateCommand(index, new Rating(parts[1]));
    }
}
