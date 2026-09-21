package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_validRemark_success() {
        assertParseSuccess(parser, " 1 r/Likes swimming! 水泳 / weekends ",
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes swimming! 水泳 / weekends")));
    }

    @Test
    public void parse_emptyOrMissingRemark_clearsRemark() {
        RemarkCommand expected = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        assertParseSuccess(parser, " 1 r/", expected);
        assertParseSuccess(parser, " 1 r/   ", expected);
        assertParseSuccess(parser, " 1 ", expected);
    }

    @Test
    public void parse_invalidIndex_failure() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", message);
        assertParseFailure(parser, " r/Note", message);
        assertParseFailure(parser, " 0 r/Note", message);
        assertParseFailure(parser, " -1 r/Note", message);
        assertParseFailure(parser, " 1.5 r/Note", message);
        assertParseFailure(parser, " abc r/Note", message);
        assertParseFailure(parser, " 2147483648 r/Note", message);
        assertParseFailure(parser, " 1 unexpected r/Note", message);
    }

    @Test
    public void parse_duplicateRemarkPrefix_failure() {
        assertParseFailure(parser, " 1 r/First r/Second",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }
}
