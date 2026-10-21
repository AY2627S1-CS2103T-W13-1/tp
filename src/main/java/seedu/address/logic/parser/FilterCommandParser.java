package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.TagsContainAnyPredicate;

/**
 * Parses repeatable tag parameters into a player-filter command.
 */
public class FilterCommandParser implements Parser<FilterCommand> {
    /**
     * Creates a parser for tag filtering.
     */
    public FilterCommandParser() {}

    /**
     * Parses one or more non-empty tag labels, combining matches with OR semantics.
     *
     * @param args The raw command arguments.
     * @return A filter command using distinct validated tags.
     * @throws ParseException If tags are missing, invalid, or preceded by unexpected text.
     */
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_TAG);
        if (!arguments.getPreamble().isEmpty() || arguments.getValue(PREFIX_TAG).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }
        return new FilterCommand(new TagsContainAnyPredicate(
                ParserUtil.parseTags(arguments.getAllValues(PREFIX_TAG))));
    }
}
