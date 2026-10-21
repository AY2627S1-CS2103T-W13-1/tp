package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's jersey number in the team.
 * Guarantees: immutable; is valid as declared in {@link #isValidJerseyNumber(String)}
 */
public class JerseyNumber {

    public static final String MESSAGE_CONSTRAINTS =
            "Jersey numbers should be whole numbers from 0 to 99, e.g. 7 or 23.";
    /** Matches one or two digits, which covers every number from 0 to 99 including a leading zero. */
    public static final String VALIDATION_REGEX = "\\d{1,2}";

    public final int value;

    /**
     * Constructs a {@code JerseyNumber}.
     * A leading zero is dropped, so "07" and "7" are the same jersey number.
     *
     * @param jerseyNumber A valid jersey number.
     */
    public JerseyNumber(String jerseyNumber) {
        requireNonNull(jerseyNumber);
        checkArgument(isValidJerseyNumber(jerseyNumber), MESSAGE_CONSTRAINTS);
        value = Integer.parseInt(jerseyNumber);
        assert value >= 0 && value <= 99 : "Jersey number should be from 0 to 99";
    }

    /**
     * Returns true if a given string is a valid jersey number.
     */
    public static boolean isValidJerseyNumber(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof JerseyNumber otherJerseyNumber)) {
            return false;
        }

        return value == otherJerseyNumber.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}
