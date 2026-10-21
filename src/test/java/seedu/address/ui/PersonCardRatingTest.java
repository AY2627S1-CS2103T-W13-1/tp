package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.JavaFxTestUtil.runOnFxThread;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardRatingTest {
    @Test
    public void constructor_ratedPlayer_showsScoreOutOfTen() throws Exception {
        runOnFxThread(() -> {
            for (String score : new String[] {"0", "8", "10"}) {
                Person player = new PersonBuilder().withRating(score).build();
                Region card = new PersonCard(player, 1).getRoot();
                Label rating = (Label) card.lookup("#rating");
                assertEquals("Rating: " + score + "/10", rating.getText());
            }
        });
    }

    @Test
    public void constructor_unratedPlayer_showsUnratedWithoutScoreSuffix() throws Exception {
        runOnFxThread(() -> {
            Person player = new PersonBuilder().build();
            Region card = new PersonCard(player, 1).getRoot();
            Label rating = (Label) card.lookup("#rating");
            assertEquals("Rating: Unrated", rating.getText());
        });
    }
}
