package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.TagsContainAnyPredicate;
import seedu.address.model.tag.Tag;

public class FilterCommandParserTest {
    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_validTags_normalizesAndCombinesLabels() {
        FilterCommand expected = new FilterCommand(new TagsContainAnyPredicate(
                Set.of(new Tag("Striker"), new Tag("Trial Group 2"))));
        assertParseSuccess(parser, "  t/ Striker t/ Trial   Group 2  t/striker  ", expected);
    }

    @Test
    public void parse_injuryStatus_aloneOrWithTags() {
        assertParseSuccess(parser, " i/INJURED",
                new FilterCommand(Optional.empty(), Optional.of(InjuryStatus.INJURED)));
        assertParseSuccess(parser, " t/Striker i/fit", new FilterCommand(Optional.of(new TagsContainAnyPredicate(
                Set.of(new Tag("Striker")))), Optional.of(InjuryStatus.FIT)));
    }

    @Test
    public void parse_invalidInjuryStatus_failure() {
        assertParseFailure(parser, " i/hurt", InjuryStatus.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " i/injured i/fit", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INJURY));
    }

    @Test
    public void parse_missingPrefixOrUnexpectedPreamble_formatError() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);
        for (String input : List.of("", "  \t", "Striker", "Striker t/Captain", "n/Striker")) {
            assertParseFailure(parser, input, message);
        }
    }

    @Test
    public void parse_invalidTags_constraintError() {
        for (String input : List.of(" t/", " t/Striker t/", " t/ t/Captain", " t/Bad!",
                " t/Striker p/123", " t/" + "a".repeat(Tag.MAX_LENGTH + 1))) {
            assertParseFailure(parser, input, Tag.MESSAGE_CONSTRAINTS);
        }
        assertParseSuccess(parser, " t/" + "a".repeat(Tag.MAX_LENGTH),
                new FilterCommand(new TagsContainAnyPredicate(Set.of(new Tag("a".repeat(Tag.MAX_LENGTH))))));
    }

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
