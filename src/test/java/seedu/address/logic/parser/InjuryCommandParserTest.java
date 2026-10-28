package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INJURY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.InjuryCommand;
import seedu.address.model.person.InjuryStatus;

public class InjuryCommandParserTest {
    private InjuryCommandParser parser = new InjuryCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() throws Exception {
        InjuryCommand expected = new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED);
        assertParseSuccess(parser, " 1 i/injured", expected);
        assertEquals(expected, new AddressBookParser().parseCommand("injury 1 i/injured"));
        assertParseSuccess(parser, " 1 i/ FIT ", new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.FIT));
        assertParseSuccess(parser, " 1 i/ InJuReD ", new InjuryCommand(INDEX_FIRST_PERSON, InjuryStatus.INJURED));
    }

    @Test
    public void parse_missingFieldsOrInvalidIndex_throwsParseException() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, InjuryCommand.MESSAGE_USAGE);
        for (String args : new String[] {"", " 1", " i/injured", " abc i/injured", " 1 extra i/injured"}) {
            assertParseFailure(parser, args, usage);
        }
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_wholeNumberNotValidIndex_throwsInvalidIndex() {
        for (String index : new String[] {"0", "-1", "2147483648"}) {
            assertParseFailure(parser, " " + index + " i/injured", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        assertParseFailure(parser, " 1 i/injured i/injured",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INJURY));
    }

    @Test
    public void parse_invalidStatus_throwsParseException() {
        for (String value : new String[] {"", "yes", "injured extra", "true", "fit n/Amy"}) {
            assertParseFailure(parser, " 1 i/" + value, InjuryStatus.MESSAGE_CONSTRAINTS);
        }
        assertThrows(NullPointerException.class, () -> ParserUtil.parseInjuryStatus(null));
    }
}
