package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_withoutJerseyNumber_showsNameAndPhone() {
        assertEquals("Tan Wei Ming; Phone: 91234567",
                Messages.format(new PersonBuilder().withName("Tan Wei Ming").withPhone("91234567").build()));
    }

    @Test
    public void format_withJerseyNumber_showsJerseyNumber() {
        assertEquals("Tan Wei Ming; Phone: 91234567; Jersey: 10", Messages.format(new PersonBuilder()
                .withName("Tan Wei Ming").withPhone("91234567").withJerseyNumber("10").build()));
    }

    @Test
    public void getErrorMessageForDuplicatePrefixes_singlePrefix_usesThatPrefixInHint() {
        assertEquals("Each of these can be given only once: n/\n"
                + "Remove the repeated one. Text such as \" n/\" inside a value also counts as a repeat.",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void getErrorMessageForDuplicatePrefixes_severalPrefixes_listsThemInOrder() {
        assertEquals("Each of these can be given only once: n/ p/\n"
                + "Remove the repeated one. Text such as \" n/\" inside a value also counts as a repeat.",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_NAME));
    }
}
