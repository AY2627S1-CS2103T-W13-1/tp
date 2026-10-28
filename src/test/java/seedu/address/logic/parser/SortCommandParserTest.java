package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class SortCommandParserTest {
    @Test
    public void parse_validInput_returnsDirection() throws Exception {
        SortCommandParser parser = new SortCommandParser();
        assertEquals(new SortCommand(false), parser.parse("rating"));
        assertEquals(new SortCommand(false), parser.parse("rating desc"));
        assertEquals(new SortCommand(true), parser.parse("  rating   asc  "));
        assertEquals(new SortCommand(true), new AddressBookParser().parseCommand("sort rating asc"));
    }

    @Test
    public void parse_anyLetterCase_returnsDirection() throws Exception {
        SortCommandParser parser = new SortCommandParser();
        assertEquals(new SortCommand(false), parser.parse("Rating"));
        assertEquals(new SortCommand(false), parser.parse("RATING Desc"));
        assertEquals(new SortCommand(true), parser.parse("rating ASC"));
        assertEquals(new SortCommand(true), new AddressBookParser().parseCommand("sort Rating Asc"));
    }

    @Test
    public void parse_invalidInput_throwsParseException() {
        for (String input : List.of("", "asc", "name", "ratings", "rating up", "rating ascending",
                "rating asc extra")) {
            assertThrows(ParseException.class, () -> new SortCommandParser().parse(input));
        }
    }
}
