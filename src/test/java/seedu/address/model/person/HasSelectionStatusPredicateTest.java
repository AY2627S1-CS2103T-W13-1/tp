package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class HasSelectionStatusPredicateTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new HasSelectionStatusPredicate(null));
    }

    @Test
    public void test_sameStatus_returnsTrue() {
        HasSelectionStatusPredicate predicate = new HasSelectionStatusPredicate(SelectionStatus.KEPT);
        assertTrue(predicate.test(new PersonBuilder().withSelectionStatus(SelectionStatus.KEPT).build()));
    }

    @Test
    public void test_differentStatus_returnsFalse() {
        HasSelectionStatusPredicate predicate = new HasSelectionStatusPredicate(SelectionStatus.KEPT);
        assertFalse(predicate.test(new PersonBuilder().withSelectionStatus(SelectionStatus.CUT).build()));
        assertFalse(predicate.test(new PersonBuilder().build()));
    }

    @Test
    public void equals() {
        HasSelectionStatusPredicate keptPredicate = new HasSelectionStatusPredicate(SelectionStatus.KEPT);

        // same object -> returns true
        assertTrue(keptPredicate.equals(keptPredicate));

        // same values -> returns true
        assertTrue(keptPredicate.equals(new HasSelectionStatusPredicate(SelectionStatus.KEPT)));

        // different types -> returns false
        assertFalse(keptPredicate.equals(1));

        // null -> returns false
        assertFalse(keptPredicate.equals(null));

        // different status -> returns false
        assertFalse(keptPredicate.equals(new HasSelectionStatusPredicate(SelectionStatus.CUT)));
    }

    @Test
    public void toStringMethod() {
        HasSelectionStatusPredicate predicate = new HasSelectionStatusPredicate(SelectionStatus.KEPT);
        String expected = HasSelectionStatusPredicate.class.getCanonicalName() + "{status=Kept}";
        assertEquals(expected, predicate.toString());
    }
}
