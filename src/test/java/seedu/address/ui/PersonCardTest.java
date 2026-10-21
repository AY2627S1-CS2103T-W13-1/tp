package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {
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
}
