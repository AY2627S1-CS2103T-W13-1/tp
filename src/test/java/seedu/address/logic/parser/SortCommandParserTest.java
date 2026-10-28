package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.Direction;
import seedu.address.logic.parser.exceptions.ParseException;

public class SortCommandParserTest {
    @Test
    public void parse_validInput_returnsDirection() throws Exception {
        SortCommandParser parser = new SortCommandParser();
        assertEquals(new SortCommand(Direction.DESCENDING), parser.parse("rating"));
        assertEquals(new SortCommand(Direction.DESCENDING), parser.parse("rating desc"));
        assertEquals(new SortCommand(Direction.ASCENDING), parser.parse("  rating   asc  "));
        assertEquals(new SortCommand(Direction.ASCENDING), new AddressBookParser().parseCommand("sort rating asc"));
    }

    @Test
    public void parse_anyLetterCase_returnsDirection() throws Exception {
        SortCommandParser parser = new SortCommandParser();
        assertEquals(new SortCommand(Direction.DESCENDING), parser.parse("Rating"));
        assertEquals(new SortCommand(Direction.DESCENDING), parser.parse("RATING Desc"));
        assertEquals(new SortCommand(Direction.ASCENDING), parser.parse("rating ASC"));
        assertEquals(new SortCommand(Direction.ASCENDING), new AddressBookParser().parseCommand("sort Rating Asc"));
    }

    @Test
    public void parse_explicitScope_returnsScopeAndDirection() throws Exception {
        SortCommandParser parser = new SortCommandParser();
        assertEquals(new SortCommand(Direction.DESCENDING, SortCommand.Scope.ALL), parser.parse("rating all"));
        assertEquals(new SortCommand(Direction.ASCENDING, SortCommand.Scope.ALL), parser.parse("rating ALL ASC"));
        assertEquals(new SortCommand(Direction.DESCENDING, SortCommand.Scope.SQUAD), parser.parse("rating squad desc"));
        assertEquals(new SortCommand(Direction.ASCENDING, SortCommand.Scope.SQUAD), parser.parse("rating Squad Asc"));
        assertEquals(new SortCommand(Direction.ASCENDING, SortCommand.Scope.ALL), parser.parse("rating asc all"));
        assertEquals(new SortCommand(Direction.DESCENDING, SortCommand.Scope.ALL), parser.parse("rating desc all"));
        assertEquals(new SortCommand(Direction.ASCENDING, SortCommand.Scope.SQUAD), parser.parse("rating ASC SQUAD"));
        assertEquals(new SortCommand(Direction.DESCENDING, SortCommand.Scope.SQUAD), parser.parse("rating desc squad"));
        for (String input : List.of("rating current", "rating squad up", "rating all squad",
                "rating squad asc extra", "rating all all", "rating asc asc", "rating asc desc")) {
            assertThrows(ParseException.class, () -> parser.parse(input));
        }
    }

    @Test
    public void parse_invalidInput_throwsParseException() {
        for (String input : List.of("", "asc", "name", "ratings", "rating up", "rating ascending",
                "rating asc extra")) {
            assertThrows(ParseException.class, () -> new SortCommandParser().parse(input));
        }
    }
}
