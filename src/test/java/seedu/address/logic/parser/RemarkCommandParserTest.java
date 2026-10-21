package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {
    private RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() throws Exception {
        RemarkCommand expected = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Strong left foot"));
        assertParseSuccess(parser, " 1 r/Strong left foot", expected);
        assertEquals(expected, new AddressBookParser().parseCommand("remark 1 r/Strong left foot"));
        assertParseSuccess(parser, " 1 r/  ", new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")));
        assertParseSuccess(parser, " 1 r/ Good  passing, pace & teamwork! n/coach",
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Good  passing, pace & teamwork! n/coach")));
    }

    @Test
    public void parse_missingFieldsOrInvalidIndex_throwsParseException() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        for (String args : new String[] {"", " 1", " r/Strong left foot", " 0 r/Strong left foot",
            " -1 r/Strong left foot", " abc r/Strong left foot",
            " 2147483648 r/Strong left foot", " 1 extra r/Strong left foot"}) {
            assertParseFailure(parser, args, usage);
        }
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        assertParseFailure(parser, " 1 r/Strong left foot r/Strong left foot",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
        assertParseFailure(parser, " 1 r/Strong left foot r/Improved passing",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
        assertParseFailure(parser, " 1 r/See r/soccer for drills",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }
}
