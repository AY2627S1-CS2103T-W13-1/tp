package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SetStatusCommand;
import seedu.address.model.person.SelectionStatus;

/**
 * As in {@code DeleteCommandParserTest}, index variations such as "1" and "1 abc" are covered by
 * {@code ParserUtilTest}, so only one valid and a few invalid inputs are tested here.
 */
public class SetStatusCommandParserTest {

    private final SetStatusCommandParser keepParser = new SetStatusCommandParser(SelectionStatus.KEPT);
    private final SetStatusCommandParser cutParser = new SetStatusCommandParser(SelectionStatus.CUT);

    @Test
    public void constructor_nullStatus_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SetStatusCommandParser(null));
    }

    @Test
    public void parse_validArgs_returnsSetStatusCommand() {
        assertParseSuccess(keepParser, "1", new SetStatusCommand(INDEX_FIRST_PERSON, SelectionStatus.KEPT));
        assertParseSuccess(cutParser, "  3  ", new SetStatusCommand(INDEX_THIRD_PERSON, SelectionStatus.CUT));
    }

    @Test
    public void parse_invalidArgs_throwsParseExceptionWithOwnUsage() {
        String keepUsage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                SetStatusCommand.getUsage(SelectionStatus.KEPT));
        assertParseFailure(keepParser, "", keepUsage);
        assertParseFailure(keepParser, "a", keepUsage);
        assertParseFailure(keepParser, "1 2", keepUsage);

        // each command word shows its own usage
        assertParseFailure(cutParser, "x", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                SetStatusCommand.getUsage(SelectionStatus.CUT)));
    }

    @Test
    public void parse_wholeNumberNotValidIndex_throwsInvalidIndex() {
        assertParseFailure(keepParser, "0", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        assertParseFailure(cutParser, "-1", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }
}
