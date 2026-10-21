package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TagTest {
    @Test
    public void constructor_normalizesWhitespaceAndPreservesCase() {
        assertEquals("Trial Group-2", new Tag("  Trial \t Group-2  ").toString());
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void isValidTagName_normalAndBoundaryLabels() {
        for (String label : List.of("A", "Striker", "Under-21", "Trial Group 2", "a".repeat(Tag.MAX_LENGTH))) {
            assertTrue(Tag.isValidTagName(label));
        }
        for (String label : List.of("", "  ", "-Striker", "Trial/2", "@role", "a".repeat(Tag.MAX_LENGTH + 1))) {
            assertFalse(Tag.isValidTagName(label));
            assertThrows(IllegalArgumentException.class, Tag.MESSAGE_CONSTRAINTS, () -> new Tag(label));
        }
    }

    @Test
    public void equals_caseInsensitiveAndConsistentHashCode() {
        Tag striker = new Tag("Striker");
        assertEquals(striker, striker);
        assertEquals(striker, new Tag("striker"));
        assertEquals(striker.hashCode(), new Tag("STRIKER").hashCode());
        assertFalse(striker.equals(new Tag("Goalkeeper")));
        assertFalse(striker.equals(null));
        assertFalse(striker.equals("Striker"));
    }
}
