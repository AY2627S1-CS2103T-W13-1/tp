package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    @Test
    public void tags_distinguishesOmittedAndClearedAndCopiesInput() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        assertTrue(descriptor.getTags().isEmpty());
        assertFalse(descriptor.isAnyFieldEdited());
        Set<Tag> labels = new HashSet<>(Set.of(new Tag("Striker")));
        descriptor.setTags(labels);
        labels.clear();
        EditPersonDescriptor copy = new EditPersonDescriptor(descriptor);
        assertEquals(Set.of(new Tag("Striker")), copy.getTags().orElseThrow());
        assertThrows(UnsupportedOperationException.class, () -> copy.getTags().orElseThrow().clear());
        descriptor.setTags(Set.of());
        assertTrue(descriptor.isAnyFieldEdited());
        assertTrue(descriptor.getTags().orElseThrow().isEmpty());
        assertFalse(descriptor.equals(copy));
        assertEquals(Set.of(new Tag("Striker")), copy.getTags().orElseThrow());
    }

    @Test
    public void equals() {
        // same values -> returns true
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", phone="
                + editPersonDescriptor.getPhone().orElse(null) + ", tags=null}";
        assertEquals(expected, editPersonDescriptor.toString());
    }
}
