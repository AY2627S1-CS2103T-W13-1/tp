package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        for (String label : List.of("A", "Striker", "Under-21", "Trial Group 2", "a".repeat(Tag.MAX_LENGTH),
                "Défenseur", "Centre Back/Wing", "Centre_Back", "U18 & Under", "C++", "St. Mary's")) {
            assertTrue(Tag.isValidTagName(label));
            assertEquals(Optional.empty(), Tag.getValidationError(label));
        }
        for (String label : List.of("", "  ", "-Striker", "@role", "Bad!", "Striker p/123",
                "a".repeat(Tag.MAX_LENGTH + 1))) {
            assertFalse(Tag.isValidTagName(label));
            assertThrows(IllegalArgumentException.class, Tag.MESSAGE_CONSTRAINTS, () -> new Tag(label));
        }
    }

    @Test
    public void equals_unicodeCaseVariants_equalWithEqualHashCodes() {
        // String#toLowerCase would turn İ into "i" plus a combining dot, which made these hash differently
        Tag dotted = new Tag("İleri");
        Tag plain = new Tag("ileri");
        assertEquals(dotted, plain);
        assertEquals(dotted.hashCode(), plain.hashCode());
        assertEquals(1, Set.of(dotted).stream().filter(Set.of(plain)::contains).count());
        assertEquals("İleri", dotted.toString());
    }

    @Test
    public void isValidTagName_supplementaryLetters_countedAsOneCharacter() {
        // 𠀀 is a letter outside the Basic Multilingual Plane, stored as two UTF-16 units
        assertTrue(Tag.isValidTagName("𠀀 Team"));
        assertTrue(Tag.isValidTagName("A𠀀 Team"));
        assertTrue(Tag.isValidTagName("𠀀".repeat(Tag.MAX_LENGTH)));
        assertFalse(Tag.isValidTagName("𠀀".repeat(Tag.MAX_LENGTH + 1)));
    }

    @Test
    public void getValidationError_eachProblem_namesTheProblem() {
        assertEquals(Optional.of(Tag.MESSAGE_EMPTY), Tag.getValidationError("  "));
        String tooLong = "a".repeat(Tag.MAX_LENGTH + 1);
        assertEquals(Optional.of(String.format(Tag.MESSAGE_TOO_LONG, tooLong, Tag.MAX_LENGTH + 1)),
                Tag.getValidationError(tooLong));
        assertEquals(Optional.of(String.format(Tag.MESSAGE_BAD_START, "-defender")),
                Tag.getValidationError("-defender"));
        assertEquals(Optional.of(String.format(Tag.MESSAGE_BAD_CHARACTER, "Bad!", "!")),
                Tag.getValidationError("Bad!"));
        assertEquals(Optional.of(String.format(Tag.MESSAGE_LOOKS_LIKE_PREFIX, "Striker p/123", "p/")),
                Tag.getValidationError("Striker   p/123"));
        assertThrows(NullPointerException.class, () -> Tag.getValidationError(null));
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
