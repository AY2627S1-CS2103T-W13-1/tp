package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import seedu.address.model.person.Person;
import seedu.address.model.person.SelectionStatus;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @Test
    public void constructor_taggedRatedKeptPlayer_showsAllDataTogether() throws Exception {
        runOnFxThread(() -> {
            Person player = new PersonBuilder().withTags("Striker")
                    .withSelectionStatus(SelectionStatus.KEPT).withRating("10").build();
            PersonCard card = new PersonCard(player, 1);
            assertEquals("Kept", lookupLabel(card, "#selectionStatus").getText());
            assertEquals("Rating: 10/10", lookupLabel(card, "#rating").getText());
            FlowPane tags = (FlowPane) card.getRoot().lookup("#tags");
            assertEquals("Striker", ((Label) tags.getChildren().getFirst()).getText());
        });
    }

    @Test
    public void constructor_withJerseyNumber_showsJerseyNumber() throws Exception {
        runOnFxThread(() -> {
            Label jerseyNumber = lookupLabel(new PersonCard(new PersonBuilder().withJerseyNumber("10").build(), 1),
                    "#jerseyNumber");

            assertEquals("Jersey #10", jerseyNumber.getText());
            assertTrue(jerseyNumber.isVisible());
            assertTrue(jerseyNumber.isManaged());
        });
    }

    @Test
    public void constructor_withoutJerseyNumber_hidesJerseyNumber() throws Exception {
        runOnFxThread(() -> {
            Label jerseyNumber = lookupLabel(new PersonCard(new PersonBuilder().build(), 1), "#jerseyNumber");

            assertFalse(jerseyNumber.isVisible());
            assertFalse(jerseyNumber.isManaged());
        });
    }

    @Test
    public void constructor_tagsAppearAlphabeticallyAndWrap() throws Exception {
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(new PersonBuilder().withTags("Striker", "Captain").build(), 1);
            FlowPane tags = (FlowPane) card.getRoot().lookup("#tags");
            assertEquals(List.of("Captain", "Striker"), tags.getChildren().stream()
                    .map(node -> ((Label) node).getText()).toList());
            PersonCard untaggedCard = new PersonCard(new PersonBuilder().build(), 2);
            assertEquals(0, ((FlowPane) untaggedCard.getRoot().lookup("#tags")).getChildren().size());
        });
    }

    @Test
    public void constructor_pendingPlayer_showsPendingBadge() throws Exception {
        assertBadge(ALICE, "Pending", "selection-status-pending");
    }

    @Test
    public void constructor_keptPlayer_showsKeptBadge() throws Exception {
        assertBadge(ALICE.withSelectionStatus(SelectionStatus.KEPT), "Kept", "selection-status-kept");
    }

    @Test
    public void constructor_cutPlayer_showsCutBadge() throws Exception {
        assertBadge(ALICE.withSelectionStatus(SelectionStatus.CUT), "Cut", "selection-status-cut");
    }

    @Test
    public void constructor_anyPlayer_showsIndexNameAndPhone() throws Exception {
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(ALICE, 4);
            assertEquals("4. ", lookupLabel(card, "#id").getText());
            assertEquals(ALICE.getName().fullName, lookupLabel(card, "#name").getText());
            assertEquals(ALICE.getPhone().value, lookupLabel(card, "#phone").getText());
        });
    }

    /**
     * Asserts that the card for {@code player} shows {@code expectedText} on its status badge,
     * styled with {@code expectedStyleClass} and none of the other statuses' style classes.
     */
    private void assertBadge(Person player, String expectedText, String expectedStyleClass) throws Exception {
        runOnFxThread(() -> {
            Label badge = lookupLabel(new PersonCard(player, 1), "#selectionStatus");
            assertEquals(expectedText, badge.getText());
            assertTrue(badge.getStyleClass().contains("selection-status"));
            assertTrue(badge.getStyleClass().contains(expectedStyleClass));
            long statusStyleCount = badge.getStyleClass().stream()
                    .filter(styleClass -> styleClass.startsWith("selection-status-"))
                    .count();
            assertFalse(statusStyleCount > 1);
        });
    }

    private Label lookupLabel(PersonCard card, String selector) {
        return (Label) card.getRoot().lookup(selector);
    }
}
