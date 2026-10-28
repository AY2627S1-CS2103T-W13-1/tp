package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RateCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Rating;

/** Parses a displayed player index and a rating, or the word "unrated" in any letter case. */
public class RateCommandParser implements Parser<RateCommand> {
    @Override
    public RateCommand parse(String args) throws ParseException {
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RateCommand.MESSAGE_USAGE));
        }
        Index index = ParserUtil.parseIndex(parts[0], RateCommand.MESSAGE_USAGE);
        if (RateCommand.UNRATED_KEYWORD.equalsIgnoreCase(parts[1])) {
            return new RateCommand(index, Rating.UNRATED);
        }
        if (!Rating.isValidRating(parts[1])) {
            throw new ParseException(RateCommand.MESSAGE_INVALID_RATING);
        }
        return new RateCommand(index, new Rating(parts[1]));
    }
}
