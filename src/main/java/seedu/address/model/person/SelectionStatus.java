package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Represents the coach's selection decision for a player.
 * A player is {@code PENDING} until the coach decides to keep or cut them.
 */
public enum SelectionStatus {
    PENDING("Pending"),
    KEPT("Kept"),
    CUT("Cut");

    public static final String MESSAGE_CONSTRAINTS = "Selection status should be one of: pending, kept, cut.";

    private final String displayName;

    SelectionStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the status whose name matches {@code value}, ignoring case and surrounding spaces.
     *
     * @throws IllegalArgumentException if {@code value} is not the name of a status.
     */
    public static SelectionStatus fromName(String value) {
        requireNonNull(value);
        String normalizedValue = value.trim().toUpperCase(Locale.ROOT);
        for (SelectionStatus status : values()) {
            if (status.name().equals(normalizedValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
    }

    /**
     * Returns the status as shown to the coach, e.g. {@code Kept}.
     */
    @Override
    public String toString() {
        return displayName;
    }
}
