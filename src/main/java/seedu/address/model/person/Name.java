package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    /** The maximum number of characters allowed in a player's name. */
    public static final int MAX_LENGTH = 100;

    /** Explains the allowed characters in a player's name. */
    public static final String MESSAGE_CONSTRAINTS =
            "Names should contain only letters (accented and non-English letters are fine), digits, spaces, "
            + "and the characters - ' / . , ( ) and should not be blank.";
    /** Explains the maximum length of a player's name. */
    public static final String MESSAGE_LENGTH_CONSTRAINTS =
            "Names should not exceed " + MAX_LENGTH + " characters.";

    /**
     * The first character of the name must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{N}'/.,()-][\\p{L}\\p{M}\\p{N} '/.,()-]*";

    /** Matches the characters ignored when checking whether two names look alike, i.e. all but letters and digits. */
    private static final String NON_ALPHANUMERIC_REGEX = "[^\\p{L}\\p{M}\\p{N}]";

    /** The player's full name, preserving the supplied capitalization. */
    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(hasValidCharacters(name), MESSAGE_CONSTRAINTS);
        checkArgument(hasValidLength(name), MESSAGE_LENGTH_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     *
     * @param test The name to validate.
     * @return Whether the name contains only allowed characters, is not blank, and does not exceed
     *         {@link #MAX_LENGTH} characters.
     */
    public static boolean isValidName(String test) {
        return hasValidCharacters(test) && hasValidLength(test);
    }

    /**
     * Returns true if a given string is non-blank and uses only the characters allowed in a name.
     */
    public static boolean hasValidCharacters(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string does not exceed {@link #MAX_LENGTH} characters.
     */
    public static boolean hasValidLength(String test) {
        return test.length() <= MAX_LENGTH;
    }

    /**
     * Returns {@code name} with leading and trailing whitespace removed and
     * repeated internal whitespace collapsed into a single space.
     */
    public static String normalize(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    /**
     * Returns true if both names refer to the same player, ignoring case and repeated internal spaces.
     * This defines a weaker notion of equality between two names, used for duplicate detection.
     */
    public boolean isSameName(Name otherName) {
        if (otherName == this) {
            return true;
        }

        return otherName != null
                && normalize(otherName.fullName).equalsIgnoreCase(normalize(fullName));
    }

    /**
     * Returns true if both names are the same once spaces and punctuation are ignored, ignoring case,
     * e.g. {@code Tan Wei Ming} and {@code TanWei-Ming}. Such names are probably the same player, but are
     * not treated as duplicates because they might genuinely differ, so callers should only warn.
     */
    public boolean isSimilarTo(Name otherName) {
        requireNonNull(otherName);
        return lettersAndDigits(fullName).equalsIgnoreCase(lettersAndDigits(otherName.fullName));
    }

    private static String lettersAndDigits(String name) {
        return name.replaceAll(NON_ALPHANUMERIC_REGEX, "");
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
