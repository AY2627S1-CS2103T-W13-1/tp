package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * An immutable player rating: a whole number from 0 to 10, or an unrated value.
 */
public class Rating {
    public static final String MESSAGE_CONSTRAINTS = "Rating must be a whole number from 0 to 10.";
    public static final Rating UNRATED = new Rating();

    private final String value;

    private Rating() {
        value = "";
    }

    /**
     * Constructs a rated value, rejecting values outside the supported range.
     */
    public Rating(String value) {
        requireNonNull(value);
        checkArgument(isValidRating(value), MESSAGE_CONSTRAINTS);
        this.value = value;
    }

    public static boolean isValidRating(String value) {
        return value.matches("[0-9]|10");
    }

    public boolean isRated() {
        return !value.isEmpty();
    }

    /** Returns the numeric rating. An unrated player has no numeric score. */
    public int getScore() {
        if (!isRated()) {
            throw new IllegalStateException("An unrated player has no score.");
        }
        return Integer.parseInt(value);
    }

    @Override
    public String toString() {
        return isRated() ? value : "Unrated";
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Rating otherRating && value.equals(otherRating.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
