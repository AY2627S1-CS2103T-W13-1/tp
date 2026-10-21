package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class JerseyNumberTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new JerseyNumber(null));
    }

    @Test
    public void constructor_invalidJerseyNumber_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new JerseyNumber(""));
        assertThrows(IllegalArgumentException.class, () -> new JerseyNumber("100"));
    }

    @Test
    public void constructor_leadingZero_droppedFromValue() {
        assertEquals(7, new JerseyNumber("07").value);
        assertEquals(0, new JerseyNumber("00").value);
        assertEquals("7", new JerseyNumber("07").toString());
    }

    @Test
    public void isValidJerseyNumber() {
        // null jersey number
        assertThrows(NullPointerException.class, () -> JerseyNumber.isValidJerseyNumber(null));

        // invalid jersey numbers
        assertFalse(JerseyNumber.isValidJerseyNumber("")); // empty string
        assertFalse(JerseyNumber.isValidJerseyNumber(" ")); // spaces only
        assertFalse(JerseyNumber.isValidJerseyNumber("100")); // above 99
        assertFalse(JerseyNumber.isValidJerseyNumber("-1")); // negative
        assertFalse(JerseyNumber.isValidJerseyNumber("+7")); // plus sign
        assertFalse(JerseyNumber.isValidJerseyNumber("7.5")); // not a whole number
        assertFalse(JerseyNumber.isValidJerseyNumber("seven")); // non-numeric
        assertFalse(JerseyNumber.isValidJerseyNumber("1 0")); // space within digits
        assertFalse(JerseyNumber.isValidJerseyNumber("#10")); // hash sign

        // valid jersey numbers
        assertTrue(JerseyNumber.isValidJerseyNumber("0")); // lowest number
        assertTrue(JerseyNumber.isValidJerseyNumber("7"));
        assertTrue(JerseyNumber.isValidJerseyNumber("07")); // leading zero
        assertTrue(JerseyNumber.isValidJerseyNumber("99")); // highest number
    }

    @Test
    public void equals() {
        JerseyNumber jerseyNumber = new JerseyNumber("10");

        // same values -> returns true
        assertTrue(jerseyNumber.equals(new JerseyNumber("10")));

        // same number with a leading zero -> returns true
        assertTrue(new JerseyNumber("7").equals(new JerseyNumber("07")));

        // same object -> returns true
        assertTrue(jerseyNumber.equals(jerseyNumber));

        // null -> returns false
        assertFalse(jerseyNumber.equals(null));

        // different types -> returns false
        assertFalse(jerseyNumber.equals(10));

        // different values -> returns false
        assertFalse(jerseyNumber.equals(new JerseyNumber("11")));
    }

    @Test
    public void hashcode() {
        assertEquals(new JerseyNumber("7").hashCode(), new JerseyNumber("07").hashCode());
        assertNotEquals(new JerseyNumber("7").hashCode(), new JerseyNumber("8").hashCode());
    }
}
