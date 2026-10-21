package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Whether a player is fit or injured, independently of any selection decision.
 */
public enum InjuryStatus {
    FIT,
    INJURED;

    public static final String MESSAGE_CONSTRAINTS = "Injury status must be 'injured' or 'fit'.";

    /**
     * Returns the status represented by the given text, ignoring case.
     *
     * @throws IllegalArgumentException if the text does not represent a supported status.
     */
    public static InjuryStatus fromString(String value) {
        requireNonNull(value);
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "fit" -> FIT;
            case "injured" -> INJURED;
            default -> throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        };
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
