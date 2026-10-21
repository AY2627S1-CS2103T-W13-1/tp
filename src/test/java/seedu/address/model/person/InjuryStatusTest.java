package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class InjuryStatusTest {
    @Test
    public void fromString_supportedValues_ignoresCase() {
        assertEquals(InjuryStatus.FIT, InjuryStatus.fromString("FIT"));
        assertEquals(InjuryStatus.INJURED, InjuryStatus.fromString("Injured"));
        assertEquals("fit", InjuryStatus.FIT.toString());
        assertEquals("injured", InjuryStatus.INJURED.toString());
    }

    @Test
    public void fromString_invalidValues_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> InjuryStatus.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> InjuryStatus.fromString("unknown"));
        assertThrows(NullPointerException.class, () -> new Person(
                new Name("Amy Bee"), new Phone("123"), (InjuryStatus) null));
    }
}
