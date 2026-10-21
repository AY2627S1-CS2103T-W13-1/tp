package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * An immutable custom player label, compared without regard to capitalization.
 */
public class Tag {
    /** Maximum normalized label length. */
    public static final int MAX_LENGTH = 30;
    /** Explains valid custom label values. */
    public static final String MESSAGE_CONSTRAINTS = "Tags must start with a letter or digit, contain only "
            + "letters, digits, spaces or hyphens, and be at most " + MAX_LENGTH + " characters long.";
    private static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} -]*";

    private final String name;

    /**
     * Creates a validated tag, trimming and collapsing whitespace while preserving capitalization.
     *
     * @param name The custom label to normalize and validate.
     * @throws IllegalArgumentException If the normalized label is invalid.
     */
    public Tag(String name) {
        requireNonNull(name);
        String normalizedName = normalize(name);
        checkArgument(isValidTagName(normalizedName), MESSAGE_CONSTRAINTS);
        this.name = normalizedName;
    }

    /**
     * Returns true if the normalized label meets the character and length constraints.
     *
     * @param name The label to validate.
     * @return Whether the label can be used as a tag.
     */
    public static boolean isValidTagName(String name) {
        requireNonNull(name);
        String normalizedName = normalize(name);
        return normalizedName.length() <= MAX_LENGTH && normalizedName.matches(VALIDATION_REGEX);
    }

    /**
     * Returns a label with surrounding whitespace removed and internal whitespace collapsed.
     */
    private static String normalize(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Tag otherTag)) {
            return false;
        }
        return name.equalsIgnoreCase(otherTag.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
