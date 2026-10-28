package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses a displayed player index and replacement tags or an explicit tag-clearing request. */
public class TagCommandParser implements Parser<TagCommand> {
    /** Creates a parser for tag commands. */
    public TagCommandParser() {
    }

    @Override
    public TagCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_TAG);
        Index index = ParserUtil.parseIndex(arguments.getPreamble(), TagCommand.MESSAGE_USAGE);
        if (arguments.getValue(PREFIX_TAG).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        }
        List<String> labels = arguments.getAllValues(PREFIX_TAG);
        boolean isClearing = labels.size() == 1 && labels.getFirst().isEmpty();
        return new TagCommand(index, isClearing ? Set.of() : ParserUtil.parseTags(labels));
    }
}
