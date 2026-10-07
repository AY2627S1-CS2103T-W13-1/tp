package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void constructor_tooLongName_throwsIllegalArgumentException() {
        String tooLongName = "a".repeat(Name.MAX_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, Name.MESSAGE_LENGTH_CONSTRAINTS, () -> new Name(tooLongName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName(" peter")); // leading space
        assertFalse(Name.isValidName("^")); // only disallowed characters
        assertFalse(Name.isValidName("peter*")); // contains disallowed characters
        assertFalse(Name.isValidName("Tan @ Ming")); // contains '@'
        assertFalse(Name.isValidName("Tan Wei Ming (GK)")); // parentheses are not allowed
        assertFalse(Name.isValidName("a".repeat(Name.MAX_LENGTH + 1))); // exceeds maximum length

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("Nur'ain Binte Hassan")); // apostrophe
        assertTrue(Name.isValidName("Muthu s/o Ramasamy")); // slash
        assertTrue(Name.isValidName("Mary-Jane Lee")); // hyphen
        assertTrue(Name.isValidName("St. John")); // full stop
        assertTrue(Name.isValidName("a".repeat(Name.MAX_LENGTH))); // exactly maximum length
    }

    @Test
    public void normalize() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.normalize(null));

        // already normalized -> unchanged
        assertEquals("Tan Wei Ming", Name.normalize("Tan Wei Ming"));

        // leading and trailing whitespace -> removed
        assertEquals("Tan Wei Ming", Name.normalize(" \t Tan Wei Ming \n"));

        // repeated internal whitespace -> collapsed into a single space
        assertEquals("Tan Wei Ming", Name.normalize("Tan   Wei \t Ming"));

        // capitalization -> preserved
        assertEquals("tAn WEI ming", Name.normalize("tAn WEI ming"));
    }

    @Test
    public void isSameName() {
        Name name = new Name("Tan Wei Ming");

        // same object -> returns true
        assertTrue(name.isSameName(name));

        // null -> returns false
        assertFalse(name.isSameName(null));

        // different case -> returns true
        assertTrue(name.isSameName(new Name("tan wei ming")));
        assertTrue(name.isSameName(new Name("TAN WEI MING")));

        // repeated internal or trailing spaces -> returns true
        assertTrue(name.isSameName(new Name("Tan  Wei   Ming")));
        assertTrue(name.isSameName(new Name("Tan Wei Ming ")));

        // different name -> returns false
        assertFalse(name.isSameName(new Name("Tan Wei Ming 2")));
        assertFalse(name.isSameName(new Name("TanWei Ming")));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));

        // different case -> returns false, as the capitalisation typed is preserved
        assertFalse(name.equals(new Name("valid name")));
    }
}
