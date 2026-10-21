package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
