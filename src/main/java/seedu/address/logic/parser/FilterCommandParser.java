package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Optional;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.InjuryStatus;
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
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_TAG, PREFIX_INJURY);
        boolean hasTags = arguments.getValue(PREFIX_TAG).isPresent();
        boolean hasInjuryStatus = arguments.getValue(PREFIX_INJURY).isPresent();
        if (!arguments.getPreamble().isEmpty() || !(hasTags || hasInjuryStatus)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_INJURY);

        Optional<TagsContainAnyPredicate> tagPredicate = Optional.empty();
        if (hasTags) {
            tagPredicate = Optional.of(new TagsContainAnyPredicate(
                    ParserUtil.parseTags(arguments.getAllValues(PREFIX_TAG))));
        }
        Optional<InjuryStatus> injuryStatus = Optional.empty();
        if (hasInjuryStatus) {
            injuryStatus = Optional.of(ParserUtil.parseInjuryStatus(arguments.getValue(PREFIX_INJURY).get()));
        }
        String commandText = FilterCommand.COMMAND_WORD + " " + args.replaceAll("[\\s\\p{Zs}]+", " ").trim();
        return new FilterCommand(tagPredicate, injuryStatus, commandText);
    }
}
