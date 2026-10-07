package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB, new AddCommand(BOB));

        // parameters in reverse order
        assertParseSuccess(parser, PHONE_DESC_BOB + NAME_DESC_BOB, new AddCommand(BOB));
    }

    @Test
    public void parse_specExamples_success() {
        assertParseSuccess(parser, " n/Tan Wei Ming p/91234567",
                new AddCommand(new PersonBuilder().withName("Tan Wei Ming").withPhone("91234567").build()));

        // reversed parameters, apostrophe in name, leading '+' in phone
        assertParseSuccess(parser, " p/+6598765432 n/Nur'ain Binte Hassan",
                new AddCommand(new PersonBuilder().withName("Nur'ain Binte Hassan").withPhone("+6598765432").build()));

        // slash accepted in name, space stripped from phone
        assertParseSuccess(parser, " n/Muthu s/o Ramasamy p/8123 4567",
                new AddCommand(new PersonBuilder().withName("Muthu s/o Ramasamy").withPhone("81234567").build()));

        // temporary distinguishing label
        assertParseSuccess(parser, " n/Trialist 12 p/90001234",
                new AddCommand(new PersonBuilder().withName("Trialist 12").withPhone("90001234").build()));
    }

    @Test
    public void parse_valuesNormalized_success() {
        Person expectedPerson = new PersonBuilder().withName("John Doe").withPhone("91234567").build();

        // leading, trailing and repeated internal spaces in name
        assertParseSuccess(parser, " n/  John   Doe   p/91234567", new AddCommand(expectedPerson));

        // spaces and hyphens in phone
        assertParseSuccess(parser, " n/John Doe p/ 9123-45 67 ", new AddCommand(expectedPerson));
    }

    @Test
    public void parse_repeatedValue_lastValueUsed() {
        // multiple names
        assertParseSuccess(parser, NAME_DESC_AMY + NAME_DESC_BOB + PHONE_DESC_BOB, new AddCommand(BOB));

        // multiple phones
        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_AMY + PHONE_DESC_BOB, new AddCommand(BOB));

        // invalid value followed by valid value
        assertParseSuccess(parser, INVALID_NAME_DESC + NAME_DESC_BOB + PHONE_DESC_BOB, new AddCommand(BOB));
        assertParseSuccess(parser, NAME_DESC_BOB + INVALID_PHONE_DESC + PHONE_DESC_BOB, new AddCommand(BOB));

        // valid value followed by invalid value
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + PHONE_DESC_BOB, expectedMessage);

        // missing phone prefix
        assertParseFailure(parser, NAME_DESC_BOB + VALID_PHONE_BOB, expectedMessage);

        // missing name entirely
        assertParseFailure(parser, PHONE_DESC_BOB, expectedMessage);

        // missing phone entirely
        assertParseFailure(parser, NAME_DESC_BOB, expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_PHONE_BOB, expectedMessage);

        // no parameters at all
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, PREAMBLE_WHITESPACE, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Tan @ Ming" + PHONE_DESC_BOB, Name.MESSAGE_CONSTRAINTS);

        // blank name, including a prefix with no value
        assertParseFailure(parser, " " + PREFIX_NAME + "   " + PHONE_DESC_BOB, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " " + PREFIX_NAME + PHONE_DESC_BOB, Name.MESSAGE_CONSTRAINTS);

        // name too long
        assertParseFailure(parser, " " + PREFIX_NAME + "a".repeat(Name.MAX_LENGTH + 1) + PHONE_DESC_BOB,
                Name.MESSAGE_LENGTH_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + " " + PREFIX_PHONE + "9123ABCD", Phone.MESSAGE_CONSTRAINTS);

        // phone too short or too long
        assertParseFailure(parser, NAME_DESC_BOB + " " + PREFIX_PHONE + "12", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + " " + PREFIX_PHONE + "1234567890123456",
                Phone.MESSAGE_CONSTRAINTS);

        // unsupported prefix is treated as part of the phone value
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + " e/amy@x.com", Phone.MESSAGE_CONSTRAINTS);

        // two invalid values, only the name error is reported
        assertParseFailure(parser, INVALID_NAME_DESC + INVALID_PHONE_DESC, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, INVALID_PHONE_DESC + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
