package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RateCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Rating;

public class RateCommandParserTest {
    @Test
    public void parse_validInput_returnsCommand() throws Exception {
        RateCommand expected = new RateCommand(Index.fromOneBased(1), new Rating("8"));
        assertEquals(expected, new RateCommandParser().parse(" 1   8 "));
        assertEquals(expected, new AddressBookParser().parseCommand("rate 1 8"));
    }

    @Test
    public void parse_unratedInAnyCase_returnsRemoveRatingCommand() throws Exception {
        RateCommand expected = new RateCommand(Index.fromOneBased(2), Rating.UNRATED);
        assertEquals(expected, new RateCommandParser().parse("2 unrated"));
        assertEquals(expected, new RateCommandParser().parse(" 2   Unrated "));
        assertEquals(expected, new AddressBookParser().parseCommand("rate 2 UNRATED"));
    }

    @Test
    public void parse_invalidRating_suggestsHowToRemoveRating() {
        ParseException exception = assertThrows(ParseException.class, () -> new RateCommandParser().parse("1 11"));
        assertEquals(RateCommand.MESSAGE_INVALID_RATING, exception.getMessage());
    }

    @Test
    public void parse_invalidInput_throwsParseException() {
        for (String input : List.of("", "1", "1 8 extra", "0 8", "-1 8", "abc 8", "1 -1", "1 11",
                "1 8.5", "1 r/8", "1 08", "2147483648 8", "1 unrate", "1 -", "0 unrated", "1 unrated 8")) {
            assertThrows(ParseException.class, () -> new RateCommandParser().parse(input));
        }
    }
}
