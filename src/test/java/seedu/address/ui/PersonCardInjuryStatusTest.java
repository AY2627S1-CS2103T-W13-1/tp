package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import seedu.address.model.person.InjuryStatus;
import seedu.address.model.person.SelectionStatus;
import seedu.address.testutil.PersonBuilder;

public class PersonCardInjuryStatusTest {
    @Test
    public void constructor_injuryStatus_displaysValue() throws Exception {
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(new PersonBuilder().withInjuryStatus(InjuryStatus.INJURED)
                    .withRemark("Strong left foot").withSelectionStatus(SelectionStatus.KEPT).build(), 1);
            Label label = (Label) card.getRoot().lookup("#injuryStatus");
            assertEquals("Injured", label.getText());
            assertTrue(label.getStyleClass().contains("injury-badge"));
            assertEquals("Remark: Strong left foot", ((Label) card.getRoot().lookup("#remark")).getText());
            assertEquals("Kept", ((Label) card.getRoot().lookup("#selectionStatus")).getText());
            assertTrue(label.isVisible());
            assertTrue(label.isManaged());
        });
    }

    @Test
    public void constructor_fitPlayer_hidesInjuryBadge() throws Exception {
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(new PersonBuilder().withInjuryStatus(InjuryStatus.FIT).build(), 1);
            Label label = (Label) card.getRoot().lookup("#injuryStatus");
            assertFalse(label.isVisible());
            assertFalse(label.isManaged());
        });
    }
}
