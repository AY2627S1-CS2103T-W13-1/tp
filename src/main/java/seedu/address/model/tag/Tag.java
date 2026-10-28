package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An immutable custom player label, compared without regard to capitalization.
 */
public class Tag {
    /** Maximum normalized label length. */
    public static final int MAX_LENGTH = 30;
    /** Lists the symbols a tag may contain besides letters, digits and spaces. */
    public static final String ALLOWED_SYMBOLS = "- & / _ + ' .";

    /** Explains valid custom label values. */
    public static final String MESSAGE_CONSTRAINTS = "Tags must start with a letter or digit, contain only "
            + "letters, digits, spaces or the symbols " + ALLOWED_SYMBOLS + ", and be at most " + MAX_LENGTH
            + " characters long.";

    /** Shown for an empty tag. */
    public static final String MESSAGE_EMPTY = "A tag cannot be empty. "
            + "To remove all of a player's tags, use \"edit INDEX t/\" with no other tags.";

    /** Shown for a tag longer than {@link #MAX_LENGTH}, with the tag and its length. */
    public static final String MESSAGE_TOO_LONG = "The tag \"%1$s\" is %2$d characters long. "
            + "Tags can be at most " + MAX_LENGTH + " characters long.";

    /** Shown for a tag that does not start with a letter or digit. */
    public static final String MESSAGE_BAD_START = "The tag \"%1$s\" must start with a letter or digit.";

    /** Shown for a tag containing a character that is not allowed, with the tag and the character. */
    public static final String MESSAGE_BAD_CHARACTER = "The tag \"%1$s\" contains \"%2$s\", which is not "
            + "allowed. Tags can contain letters, digits, spaces and the symbols " + ALLOWED_SYMBOLS + "";

    /** Shown for a tag containing text like {@code p/}, which is probably another parameter typed by mistake. */
    public static final String MESSAGE_LOOKS_LIKE_PREFIX = "The tag \"%1$s\" contains \"%2$s\", which looks like "
            + "the start of another parameter. Check the command format.";
    private static final String FIRST_CHARACTER_REGEX = "[\\p{L}\\p{N}]";
    private static final String ALLOWED_CHARACTER_REGEX = "[\\p{L}\\p{M}\\p{N} &/_+'.-]";
    /** Matches a letter followed by a slash at the start of a word, e.g. {@code p/}, as parameters are typed. */
    private static final Pattern PREFIX_LIKE_PATTERN = Pattern.compile("(?:^|\\s)(\\p{L}/)");

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
        return getValidationError(normalizedName).isEmpty();
    }

    /**
     * Returns a message explaining why {@code name} cannot be used as a tag, or an empty {@code Optional}
     * if it can. The message names the specific problem, so the coach knows what to change.
     *
     * @param name The label to check, before normalization.
     */
    public static Optional<String> getValidationError(String name) {
        requireNonNull(name);
        String normalizedName = normalize(name);
        if (normalizedName.isEmpty()) {
            return Optional.of(MESSAGE_EMPTY);
        }
        if (normalizedName.length() > MAX_LENGTH) {
            return Optional.of(String.format(MESSAGE_TOO_LONG, normalizedName, normalizedName.length()));
        }
        if (!normalizedName.substring(0, 1).matches(FIRST_CHARACTER_REGEX)) {
            return Optional.of(String.format(MESSAGE_BAD_START, normalizedName));
        }
        Matcher prefixLikeText = PREFIX_LIKE_PATTERN.matcher(normalizedName);
        if (prefixLikeText.find()) {
            return Optional.of(String.format(MESSAGE_LOOKS_LIKE_PREFIX, normalizedName, prefixLikeText.group(1)));
        }
        Optional<String> badCharacter = normalizedName.codePoints()
                .mapToObj(Character::toString)
                .filter(character -> !character.matches(ALLOWED_CHARACTER_REGEX))
                .findFirst();
        if (badCharacter.isPresent()) {
            return Optional.of(String.format(MESSAGE_BAD_CHARACTER, normalizedName, badCharacter.get()));
        }
        return Optional.empty();
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
