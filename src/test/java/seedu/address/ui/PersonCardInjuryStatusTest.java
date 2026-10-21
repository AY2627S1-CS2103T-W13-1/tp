package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import seedu.address.model.person.InjuryStatus;
import seedu.address.testutil.PersonBuilder;

public class PersonCardInjuryStatusTest {
    @Test
    public void constructor_injuryStatus_displaysValue() throws Exception {
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(new PersonBuilder().withInjuryStatus(InjuryStatus.INJURED).build(), 1);
            Label label = (Label) card.getRoot().lookup("#injuryStatus");
            assertEquals("Injury status: injured", label.getText());
            assertTrue(label.isVisible());
            assertTrue(label.isManaged());
            assertTrue(label.isWrapText());
        });
    }
}
