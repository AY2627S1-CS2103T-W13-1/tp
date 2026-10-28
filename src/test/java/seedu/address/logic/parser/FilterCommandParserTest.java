package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
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
    public void parse_missingPrefixOrUnexpectedPreamble_formatError() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);
        for (String input : List.of("", "  \t", "Striker", "Striker t/Captain", "n/Striker")) {
            assertParseFailure(parser, input, message);
        }
    }

    @Test
    public void parse_invalidTags_constraintError() {
        for (String input : List.of(" t/", " t/Striker t/", " t/ t/Captain")) {
            assertParseFailure(parser, input, Tag.MESSAGE_EMPTY);
        }
        assertParseFailure(parser, " t/Bad!", String.format(Tag.MESSAGE_BAD_CHARACTER, "Bad!", "!"));
        assertParseFailure(parser, " t/Striker p/123",
                String.format(Tag.MESSAGE_LOOKS_LIKE_PREFIX, "Striker p/123", "p/"));
        String tooLong = "a".repeat(Tag.MAX_LENGTH + 1);
        assertParseFailure(parser, " t/" + tooLong, String.format(Tag.MESSAGE_TOO_LONG, tooLong, Tag.MAX_LENGTH + 1));
        assertParseSuccess(parser, " t/" + "a".repeat(Tag.MAX_LENGTH),
                new FilterCommand(new TagsContainAnyPredicate(Set.of(new Tag("a".repeat(Tag.MAX_LENGTH))))));
    }

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
