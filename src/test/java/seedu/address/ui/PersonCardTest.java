package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {
    @Test
    public void constructor_withJerseyNumber_showsJerseyNumber() throws Exception {
        runOnFxThread(() -> {
            Region card = new PersonCard(new PersonBuilder().withJerseyNumber("10").build(), 1).getRoot();
            Label jerseyNumber = (Label) card.lookup("#jerseyNumber");

            assertEquals("Jersey #10", jerseyNumber.getText());
            assertTrue(jerseyNumber.isVisible());
            assertTrue(jerseyNumber.isManaged());
        });
    }

    @Test
    public void constructor_withoutJerseyNumber_hidesJerseyNumber() throws Exception {
        runOnFxThread(() -> {
            Region card = new PersonCard(new PersonBuilder().build(), 1).getRoot();
            Label jerseyNumber = (Label) card.lookup("#jerseyNumber");

            assertFalse(jerseyNumber.isVisible());
            assertFalse(jerseyNumber.isManaged());
        });
    }
}
