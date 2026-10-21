package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
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

    @Test
    public void layout_longRemarkInNarrowList_wrapsWithoutClipping() throws Exception {
        runOnFxThread(() -> {
            String observation = "Strong left foot and improved passing under pressure. ".repeat(12).trim();
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(
                    new PersonBuilder().withRemark(observation).build()));
            Region root = panel.getRoot();
            new Scene(root, 450, 600);
            root.resize(450, 600);
            root.applyCss();
            root.layout();

            Label label = (Label) root.lookup("#remark");
            assertNotNull(label);
            ListView<?> list = (ListView<?>) root.lookup("#personListView");
            Bounds viewport = list.localToScene(list.getLayoutBounds());
            Bounds remark = label.localToScene(label.getLayoutBounds());
            assertEquals("Remark: " + observation, label.getText());
            assertTrue(label.getHeight() > 2 * label.getFont().getSize());
            assertTrue(label.getHeight() >= label.prefHeight(label.getWidth()) - 0.5);
            assertTrue(remark.getMinX() >= viewport.getMinX());
            assertTrue(remark.getMaxX() <= viewport.getMaxX());
            assertTrue(remark.getMinY() >= viewport.getMinY());
            assertTrue(remark.getMaxY() <= viewport.getMaxY());
        });
    }
}
