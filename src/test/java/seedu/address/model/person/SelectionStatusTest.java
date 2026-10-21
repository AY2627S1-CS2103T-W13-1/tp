package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SelectionStatusTest {

    @Test
    public void fromName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SelectionStatus.fromName(null));
    }

    @Test
    public void fromName_validName_returnsStatus() {
        assertEquals(SelectionStatus.PENDING, SelectionStatus.fromName("PENDING"));
        assertEquals(SelectionStatus.KEPT, SelectionStatus.fromName("kept"));
        assertEquals(SelectionStatus.CUT, SelectionStatus.fromName("  Cut  "));
    }

    @Test
    public void fromName_invalidName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, SelectionStatus.MESSAGE_CONSTRAINTS, () ->
                SelectionStatus.fromName(""));
        // the command word is not the status name
        assertThrows(IllegalArgumentException.class, SelectionStatus.MESSAGE_CONSTRAINTS, () ->
                SelectionStatus.fromName("keep"));
    }

    @Test
    public void toStringMethod() {
        assertEquals("Pending", SelectionStatus.PENDING.toString());
        assertEquals("Kept", SelectionStatus.KEPT.toString());
        assertEquals("Cut", SelectionStatus.CUT.toString());
    }
}
