package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a free-text remark attached to a Person in the address book.
 * An empty remark means the person has no remark.
 * Guarantees: immutable; any string, including an empty one, is a valid remark.
 */
public class Remark {

    public final String value;

    /**
     * Constructs a {@code Remark}.
     *
     * @param remark A remark, which may be empty.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        value = remark;
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
        if (!(other instanceof Remark otherRemark)) {
            return false;
        }

        return value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
