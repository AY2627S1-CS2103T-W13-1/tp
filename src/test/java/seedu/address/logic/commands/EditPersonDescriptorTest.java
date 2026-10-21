package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_JERSEY_NUMBER_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

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

        // jersey number edited -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withJerseyNumber(VALID_JERSEY_NUMBER_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // jersey number removed -> returns false, as it differs from leaving the jersey number unchanged
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withoutJerseyNumber().build();
        assertFalse(DESC_AMY.equals(editedAmy));
        assertTrue(editedAmy.equals(new EditPersonDescriptor(editedAmy)));
    }

    @Test
    public void isAnyFieldEdited() {
        assertFalse(new EditPersonDescriptor().isAnyFieldEdited());
        assertTrue(new EditPersonDescriptorBuilder().withJerseyNumber(VALID_JERSEY_NUMBER_BOB).build()
                .isAnyFieldEdited());

        // removing the jersey number counts as an edit
        assertTrue(new EditPersonDescriptorBuilder().withoutJerseyNumber().build().isAnyFieldEdited());
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", phone="
                + editPersonDescriptor.getPhone().orElse(null) + ", jerseyNumber="
                + editPersonDescriptor.getJerseyNumber().orElse(null) + ", isJerseyNumberEdited=false}";
        assertEquals(expected, editPersonDescriptor.toString());
    }
}
