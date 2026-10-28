package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.model.tag.Tag;

public class TagCommandParserTest {
    private final TagCommandParser parser = new TagCommandParser();

    @Test
    public void parse_tags_normalizesDeduplicatesAndClears() {
        assertParseSuccess(parser, "1 t/ Captain t/captain t/Trial   Group 2",
                new TagCommand(Index.fromOneBased(1), Set.of(new Tag("Captain"), new Tag("Trial Group 2"))));
        assertParseSuccess(parser, " 1 t/ ", new TagCommand(Index.fromOneBased(1), Set.of()));
        assertParseSuccess(parser, "1 t/" + "a".repeat(Tag.MAX_LENGTH),
                new TagCommand(Index.fromOneBased(1), Set.of(new Tag("a".repeat(Tag.MAX_LENGTH)))));
    }

    @Test
    public void parse_missingIndexOrTags_reportsUsage() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE);
        for (String input : List.of("", "1", "t/Captain", "word t/Captain", "1 n/Amy")) {
            assertParseFailure(parser, input, usage);
        }
        assertParseFailure(parser, "0 t/Captain", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        assertParseFailure(parser, "-1 t/Captain", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_invalidTags_rejectsEntireUpdate() {
        assertParseFailure(parser, "1 t/ t/Captain", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, "1 t/Captain t/", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, "1 t/Bad!", String.format(Tag.MESSAGE_BAD_CHARACTER, "Bad!", "!"));
        String tooLong = "a".repeat(Tag.MAX_LENGTH + 1);
        assertParseFailure(parser, "1 t/" + tooLong,
                String.format(Tag.MESSAGE_TOO_LONG, tooLong, Tag.MAX_LENGTH + 1));
    }
}
