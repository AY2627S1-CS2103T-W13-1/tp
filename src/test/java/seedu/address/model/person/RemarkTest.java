package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_unrestrictedText_preservesValue() {
        assertEquals("", new Remark("").value);
        String text = "Likes swimming! 水泳 / 週末\nPrefers mornings.";
        assertEquals(text, new Remark(text).value);
        assertEquals(text, new Remark(text).toString());
    }

    @Test
    public void equals() {
        Remark remark = new Remark("Likes swimming");
        Remark copy = new Remark("Likes swimming");
        assertTrue(remark.equals(remark));
        assertTrue(remark.equals(copy));
        assertEquals(remark.hashCode(), copy.hashCode());
        assertFalse(remark.equals(null));
        assertFalse(remark.equals("Likes swimming"));
        assertFalse(remark.equals(new Remark("Likes running")));
    }
}
