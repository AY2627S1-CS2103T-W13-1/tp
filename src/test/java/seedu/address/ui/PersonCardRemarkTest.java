package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import seedu.address.testutil.PersonBuilder;

public class PersonCardRemarkTest {
    @Test
    public void constructor_remark_displaysValue() throws Exception {
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(new PersonBuilder().withRemark("Strong left foot").build(), 1);
            Label label = (Label) card.getRoot().lookup("#remark");
            assertEquals("Remark: Strong left foot", label.getText());
            assertTrue(label.isVisible());
            assertTrue(label.isManaged());
            assertTrue(label.isWrapText());
        });
    }

    @Test
    public void constructor_emptyRemark_hidesLabelAndReleasesSpace() throws Exception {
        runOnFxThread(() -> {
            Label label = (Label) new PersonCard(new PersonBuilder().build(), 1).getRoot().lookup("#remark");
            assertFalse(label.isVisible());
            assertFalse(label.isManaged());
        });
    }
}
