package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
        assertThrows(NullPointerException.class, () -> new Person(new Name("Amy Bee"), new Phone("123"), null));
    }

    @Test
    public void equals_freeTextAndEmptyValues_comparesText() {
        Remark remark = new Remark("Good passing & pace!");
        assertEquals(remark, remark);
        assertEquals(remark, new Remark("Good passing & pace!"));
        assertEquals(remark.hashCode(), new Remark("Good passing & pace!").hashCode());
        assertEquals("Good passing & pace!", remark.toString());
        assertEquals("", new Remark("").value);
        assertNotEquals(remark, new Remark(""));
        assertNotEquals(remark, null);
        assertNotEquals(remark, "Good passing & pace!");
    }
}
