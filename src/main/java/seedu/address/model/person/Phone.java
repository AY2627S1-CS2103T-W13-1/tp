package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers should contain only digits, optionally preceded by a single '+', "
            + "and should be 3 to 15 digits long. Spaces, hyphens, dots and brackets are allowed and will be "
            + "removed, e.g. (65) 9123-4567. To note what a number is for, e.g. HP, use the remark command.";
    public static final String VALIDATION_REGEX = "\\+?\\d{3,15}";
    /** Matches the separators a user may type between digits, which are removed before validation. */
    private static final String SEPARATOR_REGEX = "[ ().-]";

    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns {@code phone} with leading and trailing whitespace, and all spaces, hyphens, dots and brackets,
     * removed.
     */
    public static String normalize(String phone) {
        return phone.trim().replaceAll(SEPARATOR_REGEX, "");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
