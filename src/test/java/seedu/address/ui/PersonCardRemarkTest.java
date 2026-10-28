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

    @Test
    public void layout_remarkFittingOnOneLine_leavesNoBlankSpace() throws Exception {
        runOnFxThread(() -> {
            String longRemark = "Very strong left foot, excellent vision, needs to work on positioning at set pieces";
            assertEquals(layOutCardHeight("Fast"), layOutCardHeight(longRemark));
        });
    }

    /**
     * Returns the height of the card for a player with {@code remark}, laid out in a list wide enough
     * for the remark to fit on one line.
     */
    private double layOutCardHeight(String remark) {
        PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(
                new PersonBuilder().withRemark(remark).build()));
        Region root = panel.getRoot();
        new Scene(root, 900, 600);
        root.resize(900, 600);
        root.applyCss();
        root.layout();
        return ((Region) root.lookup("#cardPane")).getHeight();
    }
}
