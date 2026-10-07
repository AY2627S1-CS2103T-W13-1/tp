package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+65abcd";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_blankValue_throwsParseException() {
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName(""));
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName(WHITESPACE));
    }

    @Test
    public void parseName_tooLong_throwsParseException() {
        String longName = "a".repeat(Name.MAX_LENGTH + 1);
        assertThrows(ParseException.class, Name.MESSAGE_LENGTH_CONSTRAINTS, () -> ParserUtil.parseName(longName));
    }

    @Test
    public void parseName_invalidCharactersAndTooLong_reportsCharacterError() {
        String longInvalidName = "@".repeat(Name.MAX_LENGTH + 1);
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName(longInvalidName));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseName_repeatedInternalWhitespace_returnsCollapsedName() throws Exception {
        assertEquals(new Name("John Doe"), ParserUtil.parseName("John   Doe"));
        assertEquals(new Name("John Doe"), ParserUtil.parseName(" John \t Doe "));
    }

    @Test
    public void parseName_maxLengthAfterCollapsing_returnsName() throws Exception {
        // 101 characters as typed, but exactly 100 once the double space is collapsed
        String name = "a".repeat(49) + "  " + "b".repeat(50);
        assertEquals(new Name("a".repeat(49) + " " + "b".repeat(50)), ParserUtil.parseName(name));
    }

    @Test
    public void parseName_mixedCase_preservesCase() throws Exception {
        assertEquals("tAn WEI ming", ParserUtil.parseName("tAn WEI ming").fullName);
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_tooShortAfterRemovingSeparators_throwsParseException() {
        assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone("1 2"));
        assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone("- -"));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parsePhone_spacesAndHyphens_returnsPhoneWithoutSeparators() throws Exception {
        assertEquals(new Phone("81234567"), ParserUtil.parsePhone("8123 4567"));
        assertEquals(new Phone("81234567"), ParserUtil.parsePhone("8123-4567"));
        assertEquals(new Phone("+6598765432"), ParserUtil.parsePhone("+65 9876-5432"));
    }
}
