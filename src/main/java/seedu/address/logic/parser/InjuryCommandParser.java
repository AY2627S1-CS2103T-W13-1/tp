package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.InjuryCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the displayed player index and injury status for an injury command.
 */
public class InjuryCommandParser implements Parser<InjuryCommand> {
    @Override
    public InjuryCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_INJURY);
        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, InjuryCommand.MESSAGE_USAGE), e);
        }
        if (argMultimap.getValue(PREFIX_INJURY).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, InjuryCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_INJURY);
        return new InjuryCommand(index, ParserUtil.parseInjuryStatus(argMultimap.getValue(PREFIX_INJURY).get()));
    }
}
