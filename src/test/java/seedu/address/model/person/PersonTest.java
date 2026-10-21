package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void constructor_tagsDefensivelyCopiedAndUnmodifiable() {
        Set<Tag> labels = new HashSet<>(Set.of(new Tag("Striker")));
        Person player = new Person(ALICE.getName(), ALICE.getPhone(), labels);
        labels.clear();
        assertEquals(Set.of(new Tag("Striker")), player.getTags());
        assertThrows(UnsupportedOperationException.class, () -> player.getTags().clear());
        assertThrows(NullPointerException.class, () -> new Person(ALICE.getName(), ALICE.getPhone(), (Set<Tag>) null));
        labels.add(null);
        assertThrows(NullPointerException.class, () -> new Person(ALICE.getName(), ALICE.getPhone(), labels));
    }

    @Test
    public void equals_tagsAffectDataButNotPlayerIdentity() {
        Person taggedAlice = new PersonBuilder(ALICE).withTags("Striker").build();
        assertTrue(ALICE.isSamePerson(taggedAlice));
        assertNotEquals(ALICE, taggedAlice);
        Person sameTags = new PersonBuilder(ALICE).withTags("striker").build();
        assertEquals(taggedAlice, sameTags);
        assertEquals(taggedAlice.hashCode(), sameTags.hashCode());
    }

    @Test
    public void withSelectionStatus_ratedPlayer_preservesRating() {
        Person rated = new Person(ALICE.getName(), ALICE.getPhone(), new Rating("8"));
        assertEquals(SelectionStatus.PENDING, rated.getSelectionStatus());
        Person kept = rated.withSelectionStatus(SelectionStatus.KEPT);
        assertEquals(new Rating("8"), kept.getRating());
        assertEquals(SelectionStatus.KEPT, kept.getSelectionStatus());
        assertEquals(SelectionStatus.PENDING, rated.getSelectionStatus());
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, different phone -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, same phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns true
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has repeated internal spaces, all other attributes same -> returns true
        editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.replace(" ", "   ")).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different selection status -> returns false
        editedAlice = new PersonBuilder(ALICE).withSelectionStatus(SelectionStatus.KEPT).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void hashcode() {
        // same values -> returns same hashcode
        assertEquals(ALICE.hashCode(), new PersonBuilder(ALICE).build().hashCode());

        // different name -> returns different hashcode
        assertNotEquals(ALICE.hashCode(), new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build().hashCode());

        // different phone -> returns different hashcode
        assertNotEquals(ALICE.hashCode(), new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build().hashCode());

        // different selection status -> returns different hashcode
        assertNotEquals(ALICE.hashCode(),
                new PersonBuilder(ALICE).withSelectionStatus(SelectionStatus.CUT).build().hashCode());
    }

    @Test
    public void constructor_withoutSelectionStatus_isPending() {
        Person newPlayer = new Person(ALICE.getName(), ALICE.getPhone());
        assertEquals(SelectionStatus.PENDING, newPlayer.getSelectionStatus());
    }

    @Test
    public void isSamePerson_differentSelectionStatus_returnsTrue() {
        // the status is a decision about the player, not part of who they are
        Person keptAlice = new PersonBuilder(ALICE).withSelectionStatus(SelectionStatus.KEPT).build();
        assertTrue(ALICE.isSamePerson(keptAlice));
    }

    @Test
    public void withSelectionStatus_newStatus_keepsOtherFields() {
        Person cutAlice = ALICE.withSelectionStatus(SelectionStatus.CUT);
        assertEquals(SelectionStatus.CUT, cutAlice.getSelectionStatus());
        assertEquals(ALICE.getName(), cutAlice.getName());
        assertEquals(ALICE.getPhone(), cutAlice.getPhone());

        // the original player is not changed
        assertEquals(SelectionStatus.PENDING, ALICE.getSelectionStatus());
    }

    @Test
    public void withSelectionStatus_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ALICE.withSelectionStatus(null));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", selectionStatus=" + ALICE.getSelectionStatus() + ", tags=[], remark=" + ALICE.getRemark()
                + ", injuryStatus=" + ALICE.getInjuryStatus() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void remark_changesDataEqualityButPreservesIdentity() {
        Person updated = new PersonBuilder(ALICE).withRemark("Strong left foot").build();
        assertTrue(ALICE.isSamePerson(updated));
        assertNotEquals(ALICE, updated);
        assertEquals(updated, new PersonBuilder(updated).build());
        assertEquals(updated.hashCode(), new PersonBuilder(updated).build().hashCode());
        assertEquals(new Remark(""), ALICE.getRemark());
    }

    @Test
    public void withRemark_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ALICE.withRemark(null));
    }

    @Test
    public void injuryStatus_changesDataEqualityButPreservesIdentity() {
        Person updated = new PersonBuilder(ALICE).withInjuryStatus(InjuryStatus.INJURED).build();
        assertTrue(ALICE.isSamePerson(updated));
        assertNotEquals(ALICE, updated);
        assertEquals(updated, new PersonBuilder(updated).build());
        assertEquals(updated.hashCode(), new PersonBuilder(updated).build().hashCode());
        assertEquals(InjuryStatus.FIT, ALICE.getInjuryStatus());
    }

    @Test
    public void withInjuryStatus_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ALICE.withInjuryStatus(null));
    }

}
