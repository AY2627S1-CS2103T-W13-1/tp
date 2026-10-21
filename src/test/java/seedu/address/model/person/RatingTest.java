package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class RatingTest {
    @Test
    public void constructor_validScores_acceptsBoundaries() {
        for (int score = 0; score <= 10; score++) {
            Rating rating = new Rating(Integer.toString(score));
            assertTrue(rating.isRated());
            assertEquals(score, rating.getScore());
        }
    }

    @Test
    public void constructor_invalidScores_rejectsInput() {
        for (String input : List.of("", " ", "-1", "11", "8.5", "+8", "08", "8 9", "abc", "9999999999")) {
            assertFalse(Rating.isValidRating(input));
            assertThrows(IllegalArgumentException.class, () -> new Rating(input));
        }
        assertThrows(NullPointerException.class, () -> new Rating(null));
    }

    @Test
    public void unrated_distinctFromZero() {
        assertFalse(Rating.UNRATED.isRated());
        assertFalse(Rating.UNRATED.equals(new Rating("0")));
        assertEquals("Unrated", Rating.UNRATED.toString());
        assertThrows(IllegalStateException.class, () -> Rating.UNRATED.getScore());
    }

    @Test
    public void equals_sameScore_hasEqualHashCode() {
        assertEquals(new Rating("8"), new Rating("8"));
        assertEquals(new Rating("8").hashCode(), new Rating("8").hashCode());
        assertFalse(new Rating("8").equals(new Rating("9")));
        assertFalse(new Rating("8").equals(null));
        assertFalse(new Rating("8").equals("8"));
    }
}
